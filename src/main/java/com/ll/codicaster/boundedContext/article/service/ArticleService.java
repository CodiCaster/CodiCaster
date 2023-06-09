
package com.ll.codicaster.boundedContext.article.service;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ll.codicaster.base.event.EventAfterWrite;
import com.ll.codicaster.boundedContext.location.entity.Location;
import com.ll.codicaster.boundedContext.location.service.LocationService;
import com.ll.codicaster.boundedContext.weather.entity.Weather;
import com.ll.codicaster.boundedContext.weather.service.WeatherService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ll.codicaster.base.event.EventAfterLike;
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
	private final LocationService locationService;
	private final WeatherService weatherService;
	private final ImageRepository imageRepository;

	private final ApplicationEventPublisher publisher;

	private final Rq rq;
	@Value("${file.upload-dir}")
	private String uploadDir;

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

	@Transactional
	public RsData<Article> saveArticle(Member actor, ArticleCreateForm form, MultipartFile imageFile) {

		Set<String> tagSet = extractHashTagList(form.getContent());
		updateUserTagMap(actor, tagSet);


        Article article = Article.builder()
                .title(form.getTitle())
                .content(form.getContent())
                .author(actor)
                .createDate(LocalDateTime.now())
                .modifyDate(LocalDateTime.now())
                .address(rq.getAddress())
                .weatherInfo(rq.getWeatherInfo())
                .tagSet(tagSet)
                .build();

        Article savedArticle = articleRepository.save(article);
        publisher.publishEvent(new EventAfterWrite(this, rq, savedArticle));

        // 이미지 파일이 있으면 저장
        if (!imageFile.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_" + imageFile.getOriginalFilename();

            File directory = new File(uploadDir);
            // 디렉토리가 존재하지 않으면 생성
            if (!directory.exists()) {
                directory.mkdirs(); // 상위 디렉토리까지 모두 생성
            }

            File saveFile = new File(uploadDir, fileName);
            try {
                imageFile.transferTo(saveFile);
            } catch (Exception e) {
                return RsData.of("F-4", "이미지 업로드에 실패하였습니다");
            }

            Image image = Image.builder()
                    .filename(fileName)
                    .filepath("/images/" + fileName)
                    .article(article)
                    .build();


            image = imageRepository.save(image);  // 이미지를 DB에 저장

            article.setImage(image); // 이미지 정보를 게시글에 추가
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
    public Article articleDetail(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No Article found with id: " + id));
    }

    //게시물 수정
    @Transactional
    public boolean updateArticle(Member actor, Long id, ArticleCreateForm form, MultipartFile imageFile) {
        try {
            Article article = articleRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("No Article found with id: " + id));

            Set<String> existingTagSet = article.getTagSet();
            truncateUserTagMap(actor, existingTagSet);
            Set<String> newTagSet = extractHashTagList(form.getContent());
            updateUserTagMap(actor, newTagSet);

            // 게시글의 정보를 수정
            article.setTitle(form.getTitle());
            article.setContent(form.getContent());
            article.setModifyDate(LocalDateTime.now());
            article.setTagSet(newTagSet);

            // 이미지 파일이 있으면 저장
            if (!imageFile.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String fileName = uuid + "_" + imageFile.getOriginalFilename();

                File directory = new File(uploadDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                File saveFile = new File(uploadDir, fileName);
                imageFile.transferTo(saveFile);

                // 기존 이미지가 있으면 삭제
                Image oldImage = article.getImage();
                if (oldImage != null) {
                    // 실제 파일 삭제
                    File oldFile = new File(uploadDir, oldImage.getFilename());
                    if (oldFile.exists()) {
                        oldFile.delete();
                    }

                    // DB에서 기존 이미지 삭제
                    imageRepository.delete(oldImage);
                }

                // 새 이미지 정보를 설정하고 저장
                Image image = new Image();
                image.setFilename(fileName);
                image.setFilepath("/images/" + fileName);
                image.setArticle(article);

                image = imageRepository.save(image);

                article.setImage(image);
            }

            return true;
        } catch (Exception e) {
            // e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean deleteArticle(Long id) {
        //RsData 사용 필요해보임
        try {
            articleRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            // e.printStackTrace();
            return false;
        }

    }

	public String getAddress(Long id) {
		Article article = articleRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("No Article Found with id: " + id));
		return locationService.getLocation(article.getLocationId()).getAddress();
	}

	public String getWeatherInfo(Long weatherId) {
		Weather weather = weatherService.getWeather(weatherId);
		return weatherService.getWeatherInfo(weather);
	}

	public Article findArticleById(Long id) {
		return articleRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("No Article found with id: " + id));
	}

    //일년전 게시물 조회
    public List<Article> getArticlesYearAgo() {
        LocalDate today = LocalDate.now();
        LocalDate oneYearAgo = today.minusYears(1); // 오늘로부터 1년 전
        LocalDate oneYearAndMonthAgo = oneYearAgo.minusMonths(1); // 오늘로부터 1년 전 - 한달
        LocalDate oneYearPlusMonthAgo = oneYearAgo.plusMonths(1);// 오늘로부터 1년 전 + 한달
        LocalDate startDate = oneYearAndMonthAgo; // 오늘로부터 1년 전 ± 한달
        LocalDate endDate = oneYearPlusMonthAgo;

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return articleRepository.findByCreateDateBetween(startDateTime, endDateTime);
    }

    //한달전 ~ 현재 게시물 조회
    public List<Article> getArticlesLastOneMonth() {
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1); //한달전

        LocalDateTime startDateTime = oneMonthAgo.atStartOfDay();
        LocalDateTime endDateTime = LocalDateTime.now();

        return articleRepository.findByCreateDateBetween(startDateTime, endDateTime);
    }

	// 일년전 오늘 앞뒤 한달 + 한달전 ~ 오늘 게시물 조회 + 성별 필터링 => 1차 필터링
	// 성별 구분하여 2차 필터링
	// 거리순 정렬
	public List<Article> showArticlesFilterdByDateAndGender(Member member) {
		List<Article> articleLastOneMonth = getArticlesLastOneMonth();
		List<Article> articleYearAgo = getArticlesYearAgo();

		List<Article> articlesNearbyToday = Stream.concat(articleYearAgo.stream(), articleLastOneMonth.stream())
			.filter(article -> article.getAuthor().getGender().equals(member.getGender()))
			.sorted(Comparator.comparingDouble(
				article -> locationService.getDistance(locationService.getLocation(article.getLocationId()))))
			.collect(Collectors.toList());

		return articlesNearbyToday;
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
    public boolean likeArticle(Member actor, Long articleId) {
        try {

            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new NoSuchElementException("No Article found with id: " + articleId));

			Set<Member> likeSet = article.getLikedMembers();
			likeSet.add(actor);
			article.setLikedMembers(likeSet);
			//이벤트 발행
			publisher.publishEvent(new EventAfterLike(this, actor, article));
			return true;
		} catch (Exception e) {
			return false;
		}

	}

    @Transactional
    public boolean unlikeArticle(Member actor, Long articleId) {
        try {

            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new NoSuchElementException("No Article found with id: " + articleId));


            Set<Member> likeSet = article.getLikedMembers();
            likeSet.remove(actor);
            article.setLikedMembers(likeSet);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

	public void truncateUserTagMap(Member member, Set<String> tagSet) {
		Map<String, Integer> tagMap = member.getTagMap();

		//값이 0 이하일 때 예외처리 ? 필요한가 => 필요없을 듯, 큰 순서대로 사용할 예
		for (String tag : tagSet) {
			tagMap.put(tag, tagMap.get(tag) - 1);
		}
	}

	public List<Article> showMyList() {
		return articleRepository.findByAuthorId(rq.getMember().getId())
			.stream()
			.sorted(Comparator.comparingLong(Article::getId).reversed())
			.collect(Collectors.toList());
	}

	//2차 정렬
	public List<Article> sortByUserTypeAndDistance(List<Article> articleList) {
		Member user = rq.getMember();

		return articleList.stream()
			.sorted(Comparator.comparingDouble(article -> calculateTotalScore(article, user)))
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

	//거리점수 : 거리가 10키로미터 이내이면 1점부여
	private double calculateDistanceScore(Article article) {
		double distance = locationService.getDistance(locationService.getLocation(article.getLocationId()));
		return distance <= 10 ? 1 : 0;
	}

    public void whenAfterSaveLocation(Location location, Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NoSuchElementException("No Article found with id: " + articleId));
        article.setLocation(location);
    }

    public void whenAfterSaveWeather(Weather weather, Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NoSuchElementException("No Article found with id: " + articleId));
        article.setWeather(weather);
    }
}

