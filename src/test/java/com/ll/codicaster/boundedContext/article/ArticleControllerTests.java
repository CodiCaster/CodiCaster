package com.ll.codicaster.boundedContext.article;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ll.codicaster.boundedContext.article.controller.ArticleController;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ArticleControllerTests {
	@Autowired
	private MockMvc mvc;

	@Test
	@DisplayName("게시글 작성 폼 테스트")
	@WithUserDetails("user1")
	void t001() throws Exception {
		ResultActions resultActions = mvc
			.perform(get("/usr/article/write"))
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(ArticleController.class))
			.andExpect(handler().methodName("articleWrite"))
			.andExpect(status().is2xxSuccessful());
	}

	@Test
	@DisplayName("게시글 목록 테스트")
	@WithUserDetails("user1")
	void t002() throws Exception {
		ResultActions resultActions = mvc
			.perform(get("/usr/article/list"))
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(ArticleController.class))
			.andExpect(handler().methodName("articles"))
			.andExpect(status().is2xxSuccessful());
	}
//	@Test
//	@DisplayName("게시글 상세 페이지 테스트")
//	@WithUserDetails("user1")
//	void t003() throws Exception {
//		Long testArticleId = 1L;
//		ResultActions resultActions = mvc
//			.perform(get("/usr/article/detail/" + testArticleId))
//			.andDo(print());
//
//		resultActions
//			.andExpect(handler().handlerType(ArticleController.class))
//			.andExpect(handler().methodName("articleDetail"))
//			.andExpect(status().is2xxSuccessful());
//	}
//
//	@Test
//	@DisplayName("게시글 수정 폼 테스트")
//	@WithUserDetails("user1")
//	void t004() throws Exception {
//		Long testArticleId = 1L; // replace this with a test Article ID
//		ResultActions resultActions = mvc
//			.perform(get("/usr/article/modify/" + testArticleId))
//			.andDo(print());
//
//		resultActions
//			.andExpect(handler().handlerType(ArticleController.class))
//			.andExpect(handler().methodName("modifyArticle"))
//			.andExpect(status().is2xxSuccessful());
//	}
//
//	@Test
//	@DisplayName("게시글 삭제 테스트")
//	@WithUserDetails("user1")
//	void t005() throws Exception {
//		Long testArticleId = 1L; // replace this with a test Article ID
//		ResultActions resultActions = mvc
//			.perform(get("/usr/article/delete/" + testArticleId))
//			.andDo(print());
//
//		resultActions
//			.andExpect(handler().handlerType(ArticleController.class))
//			.andExpect(handler().methodName("deleteArticle"))
//			.andExpect(status().is3xxRedirection());
//	}
//
//	@Test
//	@DisplayName("게시글 좋아요 테스트")
//	@WithUserDetails("user1")
//	void t006() throws Exception {
//		Long testArticleId = 1L; // replace this with a test Article ID
//		ResultActions resultActions = mvc
//			.perform(post("/usr/article/like/" + testArticleId).with(csrf()))
//			.andDo(print());
//
//		resultActions
//			.andExpect(handler().handlerType(ArticleController.class))
//			.andExpect(handler().methodName("likeArticle"))
//			.andExpect(status().is3xxRedirection());
//	}


}

