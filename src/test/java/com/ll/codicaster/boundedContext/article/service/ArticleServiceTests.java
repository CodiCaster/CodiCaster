package com.ll.codicaster.boundedContext.article.service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;

import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.article.dto.ArticleDTO;
import com.ll.codicaster.boundedContext.article.repository.ArticleRepository;
import com.ll.codicaster.boundedContext.image.entity.Image;
import com.ll.codicaster.boundedContext.image.repository.ImageRepository;
import com.ll.codicaster.boundedContext.member.entity.Member;

@ActiveProfiles("test")
public class ArticleServiceTests {

	@Mock
	private ArticleRepository articleRepository;

	@Mock
	private ImageRepository imageRepository;

	@Mock
	private ApplicationEventPublisher publisher;

	@Mock
	private Rq rq;

	private ArticleService articleService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		articleService = new ArticleService(articleRepository, imageRepository, publisher, rq);
	}

	@Test
	@DisplayName("article 저장 테스트")
	void t001() {
		Member actor = new Member();
		ArticleDTO articleDTO = new ArticleDTO();
		articleDTO.setTitle("테스트 제목");
		articleDTO.setContent("테스트 내용");
		MultipartFile imageFile = new MockMultipartFile("test.jpg", new byte[] {});

		when(articleRepository.save(any(Article.class))).thenReturn(new Article());
		when(imageRepository.save(any(Image.class))).thenReturn(new Image());

		RsData<Article> result = articleService.saveArticle(actor, articleDTO, imageFile);

		assertEquals("S-1", result.getResultCode());
		assertEquals("성공적으로 저장되었습니다", result.getMsg());
		assertNotNull(result.getData());

	}

	@Test
	@DisplayName("article 삭제 테스트")
	void t002() {
		// Mock data
		Long articleId = 1L;
		Member member = new Member();

		Member author = new Member();

		when(articleRepository.findById(articleId)).thenReturn(Optional.of(Article.builder().author(author).build()));
		doNothing().when(articleRepository).deleteById(articleId);

		RsData<Void> result = articleService.deleteArticle(articleId, member);

		assertEquals("S-1", result.getResultCode());
		assertEquals("삭제되었습니다.", result.getMsg());
	}

	@Test
	@DisplayName("article 수정 테스트")
	void t003() {
		Long articleId = 1L;
		Member member = new Member();

		ArticleDTO articleDTO = new ArticleDTO();
		articleDTO.setTitle("수정한 제목");
		articleDTO.setContent("수정한 내용");
		MultipartFile imageFile = new MockMultipartFile("test.jpg", new byte[] {});

		Member author = new Member();
		Article existingArticle = Article.builder()
			.title("테스트 제목")
			.content("테스트 내용")
			.author(author)
			.tagSet(new HashSet<>())
			.build();


		when(articleRepository.save(any(Article.class))).thenReturn(existingArticle);
		when(articleRepository.findById(articleId)).thenReturn(Optional.of(existingArticle));

		RsData<Article> result = articleService.updateArticle(member, articleId, articleDTO, imageFile);

		assertNotNull(result);
		assertEquals("S-1", result.getResultCode());
		assertEquals("수정되었습니다.", result.getMsg());
	}

}
