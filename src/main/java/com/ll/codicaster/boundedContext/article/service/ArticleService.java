package com.ll.codicaster.boundedContext.article.service;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.article.form.ArticleCreateForm;
import com.ll.codicaster.boundedContext.article.repository.ArticleRepository;
import com.ll.codicaster.boundedContext.image.entity.Image;
import com.ll.codicaster.boundedContext.image.repository.ImageRepository;
import com.ll.codicaster.boundedContext.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticleService {

	private final ArticleRepository articleRepository;
	private final ImageRepository imageRepository;

	private final Rq rq;
	@Value("${file.upload-dir}")
	private String uploadDir;

	public static List<String> extractHashTagList(String content) {
		List<String> tagList = new ArrayList<>();

		Pattern pattern = Pattern.compile("#([ㄱ-ㅎ가-힣a-zA-Z0-9_]+)");
		Matcher matcher = pattern.matcher(content);

		while (matcher.find()) {
			String tag = matcher.group(1);
			tagList.add(tag);
		}

		return tagList;
	}

	public void saveArticle(Member actor, ArticleCreateForm form, MultipartFile imageFile) throws Exception {
		List<String> TagList = extractHashTagList(form.getContent());
		updateUserTagMap(actor, TagList);
		Article article = Article.builder()
			.title(form.getTitle())
			.content(form.getContent())
			.author(actor)
			.createDate(LocalDateTime.now())
			.modifyDate(LocalDateTime.now())
			.tagList(TagList)
			.build();

		articleRepository.save(article);

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
			imageFile.transferTo(saveFile);

			Image image = new Image();
			image.setFilename(fileName);
			image.setFilepath("/images/" + fileName);
			image.setArticle(article);  // Image 객체와 Article 객체를 연결

			image = imageRepository.save(image);  // 이미지를 DB에 저장

			article.setImage(image); // 이미지 정보를 게시글에 추가
		}

	}

	//게시물 전체 리스트
	public List<Article> articleList() {

		return articleRepository.findAll();
	}

	//게시물 상세
	public Article articleDetail(Long id) {
		return articleRepository.findById(id)
			.orElseThrow(() -> new NoSuchElementException("No Article found with id: " + id));
	}

	//게시물 수정
	@Transactional
	public boolean updateArticle(Long id, ArticleCreateForm form, MultipartFile imageFile) {
		try {
			Article article = articleRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("No Article found with id: " + id));

			// 게시글의 정보를 수정
			article.setTitle(form.getTitle());
			article.setContent(form.getContent());
			article.setModifyDate(LocalDateTime.now());

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
		try {
			articleRepository.deleteById(id);
			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}

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

	public List<Article> showArticlesNearbyToday() {
		List<Article> articleLastOneMonth = getArticlesLastOneMonth();
		List<Article> articleYearAgo = getArticlesYearAgo();

		List<Article> ArticlesNearbyToday = Stream.concat(articleYearAgo.stream(), articleLastOneMonth.stream())
			.collect(Collectors.toList());

		return ArticlesNearbyToday;

	}

	public void updateUserTagMap(Member member, List<String> TagList) {
		Map<String, Integer> tagMap = member.getTagMap();

		for (String tag : TagList) {
			tagMap.put(tag, tagMap.getOrDefault(tag, 0) + 1);
		}

	}

}
