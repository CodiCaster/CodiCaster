package com.ll.codicaster.member;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.ll.codicaster.boundedContext.member.entity.Member;
import com.ll.codicaster.boundedContext.member.service.MemberService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MemberServiceTests {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private MemberService memberService;

	@Test
	@DisplayName("사용자 정보 업데이트")
	void t001() throws Exception {
		// GIVEN
		String newNickname = "new_nickname";
		String newBodyType = "new_bodyType";
		String newGender = "new_gender";

		// WHEN
		ResultActions resultActions = mvc
			.perform(post("/usr/member/newInfo")
				.with(csrf())
				.param("nickname", newNickname)
				.param("bodyType", newBodyType)
				.param("gender", newGender)
				.with(user("user1").password("1234")) // 로그인된 상태를 시뮬레이트
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/main"));

		Member updatedMember = memberService.findByUsername("user1").orElseThrow();
		assertThat(updatedMember.getNickname()).isEqualTo(newNickname);
		assertThat(updatedMember.getBodyType()).isEqualTo(newBodyType);
		assertThat(updatedMember.getGender()).isEqualTo(newGender);
	}
}
