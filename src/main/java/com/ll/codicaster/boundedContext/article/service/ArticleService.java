package com.ll.codicaster.boundedContext.article.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;



import com.ll.codicaster.boundedContext.aws.s3.dto.AmazonS3ImageDto;
import com.ll.codicaster.boundedContext.aws.s3.repository.AmazonS3Repository;
import com.ll.codicaster.boundedContext.aws.s3.service.AmazonS3Service;
import com.ll.codicaster.base.event.EventAfterWrite;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ll.codicaster.base.event.EventAfterLike;
import com.ll.codicaster.base.event.EventBeforeDeleteArticle;
import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.article.form.ArticleCreateForm;
import com.ll.codicaster.boundedContext.article.repository.ArticleRepository;
import com.ll.codicaster.boundedContext.image.entity.Image;
import com.ll.codicaster.boundedContext.image.repository.ImageRepository;
import com.ll.codicaster.boundedContext.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {

	private final ArticleRepository articleRepository;
	private final ImageRepository imageRepository;
	private final ApplicationEventPublisher publisher;
    private final AmazonS3Service amazonS3Service;
	private final Rq rq;



	public static Set<String> extractHashTagList(String content) {
		Set<String> tagSet = new HashSet<>();

		Pattern pattern = Pattern.compile("#([ㄱ-ㅎ가-힣a-zA-Z0-9_]+)");
		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			String tag = matcher.group(1);
			tagSet.add(tag);
		}

		return tagSet;
	}

	//10진수를 radian(라디안)으로 변환
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	//radian(라디안)을 10진수로 변환
	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}

	@Transactional
	public RsData<Article> saveArticle(Member actor, ArticleCreateForm form, MultipartFile imageFile) {

		Set<String> tagSet = extractHashTagList(form.getContent());
		updateUserTagMap(actor, tagSet);

		Article article = Article.builder()
			.title(form.getTitle())
			.content(form.getContent())
			.author(actor)
			.address(rq.getAddress())
			.weatherInfo(rq.getWeatherInfo())
			.tagSet(tagSet)
			.build();

		Article savedArticle = articleRepository.save(article);
		publisher.publishEvent(new EventAfterWrite(this, rq.getCurrentLocation(), savedArticle));

        // 클라우드스토리지 사용
        if (!imageFile.isEmpty()) {
            try {
                // 이미지 업로드 및 URL 정보 받아오기
                AmazonS3ImageDto amazonS3ImageDto = amazonS3Service.imageUpload(imageFile, UUID.randomUUID().toString());

                // 이미지 정보를 설정하고 저장
                Image image = Image.builder()
                    .filename(imageFile.getOriginalFilename())
                    .filepath(amazonS3ImageDto.getCdnUrl()) // CDN URL로 변경
                    .article(article)
                    .build();

                image = imageRepository.save(image);  // 이미지를 DB에 저장

                article.setImage(image); // 이미지 정보를 게시글에 추가
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("이미지 업로드에 실패하였습니다", e);
            }
        }
        return RsData.of("S-1", "성공적으로 저장되었습니다", article);
    }




    //게시물 전체 리스트
    public List<Article> articleList() {
        return articleRepository.findAll()
                .stream()
                .sorted(Comparator.comparingLong(Article::getId).reversed())
                .collect(Collectors.toList());
    }

	//게시물 상세
	public Optional<Article> findById(Long id) {
		return articleRepository.findById(id);
	}

	//게시물 수정
	@Transactional
	public RsData<Article> updateArticle(Member member, Long id, ArticleCreateForm form, MultipartFile imageFile) {
		if (member == null) {
			return RsData.of("F-1", "수정 권한이 없습니다.");
		}
		Article article = articleRepository.findById(id).orElse(null);
		if (article == null) {
			return RsData.of("F-2", "게시물이 존재하지 않습니다.");
		}


		if (!Objects.equals(article.getAuthor().getId(), member.getId())) {
			return RsData.of("F-3", "수정 권한이 없습니다.");
		}

		Set<String> existingTagSet = article.getTagSet();
		truncateUserTagMap(member, existingTagSet);
		Set<String> newTagSet = extractHashTagList(form.getContent());
		updateUserTagMap(member, newTagSet);

		// 게시글의 정보를 수정
		article.setTitle(form.getTitle());
		article.setContent(form.getContent());
		article.setTagSet(newTagSet);

		// 클라우드 스토리지 이용
		if (!imageFile.isEmpty()) {
			try {
				// 이미지 업로드 및 URL 정보 받아오기
				AmazonS3ImageDto amazonS3ImageDto = amazonS3Service.imageUpload(imageFile, UUID.randomUUID().toString());

				// 기존 이미지 삭제
				Image oldImage = article.getImage();
				if (oldImage != null) {
					String imageUrl = oldImage.getFilepath();
					amazonS3Service.deleteImage(imageUrl);
					imageRepository.delete(oldImage);
				}

				// 새 이미지 정보를 설정하고 저장
				Image image = Image.builder()
					.filename(imageFile.getOriginalFilename())
					.filepath(amazonS3ImageDto.getCdnUrl())
					.article(article)
					.build();

				image = imageRepository.save(image);

				article.setImage(image);
			} catch (Exception e) {
				return RsData.of("F-4", "이미지 저장 중 오류가 발생했습니다.");
			}
		} else {
			// 이미지를 수정하지 않는 경우, 기존 이미지를 그대로 유지
			article.setImage(article.getImage());
		}

        try {
            article = articleRepository.save(article);
        } catch (Exception e) {
            return RsData.of("F-5", "게시글 수정 중 오류가 발생했습니다.");
        }

        return RsData.of("S-1", "성공적으로 수정되었습니다.", article);
    }



	@Transactional
	public RsData deleteArticle(Long id, Member member) {
		if (member == null) {
			return RsData.of("F-1", "삭제 권한이 없습니다.");
		}
		Article article = articleRepository.findById(id).orElse(null);
		if (article == null) {
			return RsData.of("F-2", "존재하지 않는 게시물입니다.");
		}
		if (article.getAuthor() == null) {
			return RsData.of("F-3", "작성자가 존재하지 않는 게시물입니다.");
		}
		if (!Objects.equals(article.getAuthor().getId(), member.getId())) {
			return RsData.of("F-4", "삭제 권한이 없습니다.");
		}

		String imageUrl = article.getImage().getFilepath();
		amazonS3Service.deleteImage(imageUrl);

		publisher.publishEvent(new EventBeforeDeleteArticle(this, article));
		articleRepository.deleteById(id);
		return RsData.of("S-1", "삭제되었습니다.");
	}



	//이게 1차 필터링.
	//가질 수 있는 날짜랑 기본 거리로 우선 정렬. 메인페이지에 위치 호출 기능 가져오면 현 위치 기준으로 정렬
	public List<Article> showArticlesFilteredByDate() {
		List<Article> articlesNearbyToday = getFilteredArticlesBetweenDates()
			.sorted(Comparator.comparingDouble(article -> getDistanceBetweenUser(article)))
			.collect(Collectors.toList());

		return articlesNearbyToday;
	}

	//1년전 오늘 ±한달 + 한달전~ 오늘 게시물 반환
	private Stream<Article> getFilteredArticlesBetweenDates() {
		LocalDate today = LocalDate.now();
		LocalDate oneYearAgo = today.minusYears(1); // 오늘로부터 1년 전
		LocalDate oneYearAndMonthAgo = oneYearAgo.minusMonths(1); // 오늘로부터 1년 전 - 한달
		LocalDate oneYearPlusMonthAgo = oneYearAgo.plusMonths(1); // 오늘로부터 1년 전 + 한달

		LocalDateTime startDateTime = oneYearAndMonthAgo.atStartOfDay();
		LocalDateTime endDateTime = oneYearPlusMonthAgo.atTime(23, 59, 59);

		List<Article> articlesYearAgo = articleRepository.findByCreateDateBetween(startDateTime, endDateTime);

		LocalDate oneMonthAgo = today.minusMonths(1); // 한달 전

		startDateTime = oneMonthAgo.atStartOfDay();
		endDateTime = LocalDateTime.now();

		List<Article> articlesLastOneMonth = articleRepository.findByCreateDateBetween(startDateTime, endDateTime);

		return Stream.concat(articlesYearAgo.stream(), articlesLastOneMonth.stream());
	}

	//유저 태그맵 업데이트 (게시물 작성 시마다 태그리스트 받아서 가지고 있는지 확인하고 증가)
	@Transactional
	public void updateUserTagMap(Member member, Set<String> tagSet) {
		Map<String, Integer> tagMap = member.getTagMap();

		for (String tag : tagSet) {
			tagMap.put(tag, tagMap.getOrDefault(tag, 0) + 1);
		}
	}

	@Transactional
	public RsData likeArticle(Member member, Long articleId) {
		if (member == null) {
			return RsData.of("F-1", "로그인 후 사용가능한 기능입니다.");
		}
		Article article = articleRepository.findById(articleId).orElse(null);
		if (article == null) {
			return RsData.of("F-2", "존재하지 않는 게시물입니다.");
		}
		article.addLikeMember(member);
		Set<Member> likeSet = article.getLikedMembers();
		likeSet.add(member);
		article.setLikedMembers(likeSet);
		//이벤트 발행
		publisher.publishEvent(new EventAfterLike(this, member, article));
		return RsData.of("S-1", "좋아요가 추가되었습니다.");
	}

	@Transactional
	public RsData unlikeArticle(Member member, Long articleId) {
		if (member == null) {
			return RsData.of("F-1", "로그인 후 사용가능한 기능입니다.");
		}
		Article article = articleRepository.findById(articleId).orElse(null);
		if (article == null) {
			return RsData.of("F-2", "존재하지 않는 게시물입니다.");
		}
		Set<Member> likeSet = article.getLikedMembers();
		likeSet.remove(member);
		article.setLikedMembers(likeSet);
		return RsData.of("S-1", "좋아요가 취소되었습니다.");
	}

	//수정시 태그맵에서 카운트 -1
	public void truncateUserTagMap(Member member, Set<String> tagSet) {
		Map<String, Integer> tagMap = member.getTagMap();

		//값이 0 이하일 때 예외처리 ? 필요한가 => 필요없을 듯, 큰 순서대로 사용할 예정
		for (String tag : tagSet) {
			tagMap.put(tag, tagMap.get(tag) - 1);
		}
	}

	//나의 게시물 (페이징 처리중, 오버로딩)
	public Page<Article> showMyList(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		return articleRepository.findByAuthorId(rq.getMember().getId(), pageable);
	}

	//나의 게시물 (리스트)
	public List<Article> showMyList() {
		return articleRepository.findByAuthorId(rq.getMember().getId())
			.stream()
			.sorted(Comparator.comparingLong(Article::getId).reversed())
			.collect(Collectors.toList());
	}

	//2차 정렬
	public List<Article> sortByAllParams(Member user, List<Article> articleList) {

		return articleList.stream()
			.sorted(Comparator.comparingDouble(article -> calculateTotalScore((Article)article, user)).reversed())
			.filter(article -> article.getAuthor().getGender().equals(user.getGender()))
			.collect(Collectors.toList());
	}

	//총 합계점수
	private double calculateTotalScore(Article article, Member user) {
		double distanceScore = calculateDistanceScore(article);
		double userTypeScore = calculateUserTypeScore(article, user);
		double likeScore = calculateLikeScore(article);
		double tagScore = calculateTagScore(article, user);

		return distanceScore + userTypeScore + likeScore + tagScore;
	}

	//체질점수 : 유저의 체질점수 ±1 이면 +1점
	private double calculateUserTypeScore(Article article, Member user) {
		int userTypeDifference = Math.abs(article.getAuthor().getBodyType() - user.getBodyType());
		return userTypeDifference <= 1 ? 1 : 0;
	}

	//거리점수 : 거리가 10키로미터 이내이면 1점부여
	private double calculateDistanceScore(Article article) {
		double distance = getDistanceBetweenUser(article);
		return distance <= 10 ? 1 : 0;
	}

	//좋아요 점수 : 좋아요 하나당 0.01점 부여
	private double calculateLikeScore(Article article) {
		return article.getLikesCount() * 0.01;
	}

	//태그점수 : 유저가 사용한 태그와 게시물 태그셋 일치하는 태그 하나당 0.5점 부여
	//성향 판단의 근거
	private double calculateTagScore(Article article, Member user) {
		List<String> userTags = user.getMostUsedTags();
		Set<String> articleTags = article.getTagSet();

		double tagScore = 0.0;

		for (String tag : userTags) {
			if (articleTags.contains(tag)) {
				tagScore += 0.5;
			}
		}

		return tagScore;
	}

	//거리 구하는 메서드
	public double getDistanceBetweenUser(Article article) {
		double nowLat = rq.getCurrentLocation().getLatitude();
		double nowLon = rq.getCurrentLocation().getLongitude();

		double lat2 = article.getLocation().getLatitude();
		double lon2 = article.getLocation().getLongitude();
		//킬로미터 단위로 거리
		double theta = nowLon - lon2;
		double dist =
			Math.sin(deg2rad(nowLat)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(nowLat)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515 * 1609.344;

		return dist / 1000;

	}

	//날짜 기준으로 필터링, 거리순으로 정렬된 리스트 => 페이지로 변환
	public ResponseEntity<Page<Article>> getPageableArticlesFilteredByDate(int page, int size) {
		List<Article> nonMembersList = showArticlesFilteredByDate();
		RsData pageRs = canGetPage(page, size, nonMembersList);
		List<Article> filteredList = nonMembersList.stream()
			.filter(article -> article.getContent() != null)
			.collect(Collectors.toList());

		//게시물 가져오기 실패하면 익셉션
		if (pageRs.isFail()) {
			throw new IllegalArgumentException(pageRs.getMsg());
		}

		return getPageResponseEntity(page, size, filteredList);
	}

	public ResponseEntity<Page<Article>> getPageableSortedArticles(Member member, int page, int size) {
		List<Article> nonmemberArticles = showArticlesFilteredByDate();
		List<Article> membersList = sortByAllParams(member, nonmemberArticles);

		RsData pageRs = canGetPage(page, size, membersList);

		// content가 null인 Article 객체 제외
		List<Article> filteredList = membersList.stream()
			.filter(article -> article.getContent() != null)
			.collect(Collectors.toList());

		//게시물 가져오기 실패하면 익셉션
		if (pageRs.isFail()) {
			throw new IllegalArgumentException(pageRs.getMsg());
		}
		return getPageResponseEntity(page, size, filteredList);
	}

	private ResponseEntity<Page<Article>> getPageResponseEntity(int page, int size, List<Article> membersList) {
		Pageable pageable = PageRequest.of(page, size);
		int start = (int)pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), membersList.size());

		Page<Article> pageResult = new PageImpl<>(membersList.subList(start, end), pageable, membersList.size());

		return ResponseEntity.ok(pageResult);
	}

	public Page<Article> getPageableMyArticles(int page, int size) {
		List<Article> myArticles = showMyList();
		Pageable pageable = PageRequest.of(page, size);
		int start = (int)pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), myArticles.size());
		return new PageImpl<>(myArticles.subList(start, end), pageable, myArticles.size());
	}

	public RsData canGetPage(int page, int size, List<?> list) {
		if (page < 0 || size <= 0) {
			return RsData.of("F-1", "page, size는 자연수여야 합니다.");
		}
		int totalArticles = list.size();
		int totalPages = (int)Math.ceil((double)totalArticles / size);
		Pageable pageable = PageRequest.of(page, size);
		int start = (int)pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), list.size());

		if (start > end) {
			return RsData.of("F-2", "마지막 페이지입니다.", "");
		}

		return RsData.of("S-1", "페이징 처리 성공");

	}

	//팔로우 한 사람의 게시물
	public List<Article> getFolloweesArticles() {
		List<Member> followingMembers = rq.getFollwingMembers();
		List<Long> followingMemberIds = followingMembers.stream().map(Member::getId).collect(Collectors.toList());
		List<Article> articles = articleRepository.findByAuthorIdIn(followingMemberIds);

		List<Article> sortedArticles = articles.stream()
			.sorted(Comparator.comparing(Article::getId).reversed())
			.collect(Collectors.toList());

		return sortedArticles;
	}

	public ResponseEntity<Page<Article>> getPageableArticles(int page, int size) {
		Page<Article> articleList = Page.empty(); // 빈 페이지 객체 생성

		if (rq.isLogout()) {
			articleList = getPageableArticlesFilteredByDate(page, size).getBody();
		} else if (rq.isLogin()) {
			articleList = getPageableSortedArticles(rq.getMember(), page, size).getBody();
		}

		return ResponseEntity.ok(articleList);
	}

	public ResponseEntity<Page<Article>> getPageableFolloweesArticles(int page, int size) {
		List<Article> followeesArticles = getFolloweesArticles(); // 팔로우 중인 사용자의 게시물 가져오기

		RsData pageRs = canGetPage(page, size, followeesArticles);

		// content가 null인 Article 객체 제외
		List<Article> filteredList = followeesArticles.stream()
			.filter(article -> article.getContent() != null)
			.collect(Collectors.toList());

		// 게시물 가져오기 실패하면 익셉션
		if (pageRs.isFail()) {
			throw new IllegalArgumentException(pageRs.getMsg());
		}

		return getPageResponseEntity(page, size, filteredList);
	}

}
