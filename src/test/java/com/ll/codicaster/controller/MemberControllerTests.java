package com.ll.codicaster.controller;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.ll.codicaster.boundedContext.member.controller.MemberController;
import com.ll.codicaster.boundedContext.member.service.MemberService;

import jakarta.servlet.http.HttpSession;

;

@SuppressWarnings("checkstyle:RegexpSinglelineJava")
@SpringBootTest // 스프링부트 관련 컴포넌트 테스트할 때 붙여야 함, Ioc 컨테이너 작동시킴
@AutoConfigureMockMvc // http 요청, 응답 테스트
@Transactional // 실제로 테스트에서 발생한 DB 작업이 영구적으로 적용되지 않도록, test + 트랜잭션 => 자동롤백
@ActiveProfiles("test") // application-test.yml 을 활성화 시킨다.
@TestMethodOrder(MethodOrderer.MethodName.class)
public class MemberControllerTests {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private MemberService memberService;

	@Test
	@DisplayName("로그인 폼")
	void t004() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(get("/usr/member/login?admin"))
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(MemberController.class))
			.andExpect(handler().methodName("showLogin"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<input type="text" name="username"
				""".stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<input type="password" name="password"
				""".stripIndent().trim())))
			.andExpect(content().string(containsString("""
				id="btn-login-1"
				""".stripIndent().trim())));
	}

	@Test
	// @Rollback(value = false) // DB에 흔적이 남는다.
	@DisplayName("로그인 처리")
	void t005() throws Exception {
		// WHEN
		ResultActions resultActions = mvc
			.perform(post("/usr/member/login")
				.with(csrf()) // CSRF 키 생성
				.param("username", "user1")
				.param("password", "1234")
			)
			.andDo(print());

		// 세션에 접근해서 user 객체를 가져온다.
		MvcResult mvcResult = resultActions.andReturn();
		HttpSession session = mvcResult.getRequest()
			.getSession(false);// 원래 getSession 을 하면, 만약에 없을 경우에 만들어서라도 준다., false 는 없으면 만들지 말라는 뜻
		SecurityContext securityContext = (SecurityContext)session.getAttribute("SPRING_SECURITY_CONTEXT");
		User user = (User)securityContext.getAuthentication().getPrincipal();

		assertThat(user.getUsername()).isEqualTo("user1");

		// THEN
		resultActions
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/**"));
	}

}
