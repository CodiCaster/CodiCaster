package com.ll.codicaster.member;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StringUtils;

import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.member.entity.Member;
import com.ll.codicaster.boundedContext.member.repository.MemberRepository;
import com.ll.codicaster.boundedContext.member.service.MemberService;

@SpringBootTest
@ActiveProfiles("test")
public class MemberServiceTests{

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private Rq rq;

	@InjectMocks
	private MemberService memberService;

	@Test
	@DisplayName("회원가입 실패 - 중복 아이디")
	void t001() {
		// Arrange
		String username = "testuser";
		String password = "password";

		when(memberRepository.findByUsername(username)).thenReturn(Optional.of(new Member()));

		// Act
		RsData<Member> rsData = memberService.join(username, password);

		// Assert
		assertTrue(rsData.isFail());
		assertEquals("F-1", rsData.getCode());
		assertEquals("해당 아이디(testuser)는 이미 사용중입니다.", rsData.getMsg());
		assertNull(rsData.getData());
	}

	@Test
	@DisplayName("소셜 로그인 - 기존 회원")
	void t002() {
		// Arrange
		String providerTypeCode = "KAKAO";
		String username = "testuser";
		Member existingMember = Member.builder()
			.providerTypeCode(providerTypeCode)
			.username(username)
			.build();

		when(memberRepository.findByUsername(username)).thenReturn(Optional.of(existingMember));

		// Act
		RsData<Member> rsData = memberService.whenSocialLogin(providerTypeCode, username);

		// Assert
		assertTrue(rsData.isSuccess());
		assertEquals("S-2", rsData.getCode());
		assertEquals("로그인 되었습니다.", rsData.getMsg());
		assertEquals(existingMember, rsData.getData());
	}

	@Test
	@DisplayName("닉네임 확인 - 중복 없음")
	void t003() {
		String nickname = "testuser";

		when(memberRepository.findByNickname(nickname)).thenReturn(Optional.empty());

		// Act
		RsData<String> rsData = memberService.checkNickname(nickname);

		// Assert
		assertTrue(rsData.isSuccess());
		assertEquals("S-1", rsData.getCode());
		assertEquals("사용 가능한 닉네임입니다.", rsData.getMsg());
		assertNull(rsData.getData());
	}

	@Test
	@DisplayName("닉네임 확인 - 중복 있음")
	void t004() {
		String nickname = "testuser";

		when(memberRepository.findByNickname(nickname)).thenReturn(Optional.of(new Member()));

		// Act
		RsData<String> rsData = memberService.checkNickname(nickname);

		// Assert
		assertTrue(rsData.isFail());
		assertEquals("F-1", rsData.getCode());
		assertEquals("해당 닉네임(testuser)은 이미 사용중입니다.", rsData.getMsg());
		assertNull(rsData.getData());
	}

	@Test
	@DisplayName("회원 정보 업데이트 - 로그인 회원")
	void t005() {
		String nickname = "newnickname";
		int bodyType = 1;
		String gender = "male";
		Member loggedInMember = new Member();

		when(rq.getMember()).thenReturn(loggedInMember);

		// Act
		RsData<Void> rsData = memberService.updateMemberInfo(nickname, bodyType, gender);

		// Assert
		assertTrue(rsData.isSuccess());
		assertEquals("S-2", rsData.getCode());
		assertEquals("회원 정보가 업데이트되었습니다.", rsData.getMsg());
		assertNull(rsData.getData());
		assertEquals(nickname, loggedInMember.getNickname());
		assertEquals(bodyType, loggedInMember.getBodyType());
		assertEquals(gender, loggedInMember.getGender());
	}

	@Test
	@DisplayName("회원 정보 업데이트 - 비로그인 회원")
	void t006() {
		String nickname = "newnickname";
		int bodyType = 1;
		String gender = "male";

		when(rq.getMember()).thenReturn(null);

		// Act
		RsData<Void> rsData = memberService.updateMemberInfo(nickname, bodyType, gender);

		// Assert
		assertTrue(rsData.isFail());
		assertEquals("F-2", rsData.getCode());
		assertEquals("로그인이 필요합니다.", rsData.getMsg());
		assertNull(rsData.getData());
	}

	@Test
	@DisplayName("회원 탈퇴 - 로그인 회원")
	void t007() {
		Member loggedInMember = new Member();

		when(rq.getMember()).thenReturn(loggedInMember);

		// Act
		RsData<Void> rsData = memberService.deleteMember();

		// Assert
		assertTrue(rsData.isSuccess());
		assertEquals("S-3", rsData.getCode());
		assertEquals("회원 탈퇴가 완료되었습니다.", rsData.getMsg());
		assertNull(rsData.getData());
		verify(memberRepository, times(1)).delete(loggedInMember);
	}

	@Test
	@DisplayName("회원 탈퇴 - 비로그인 회원")
	void t008() {
		when(rq.getMember()).thenReturn(null);

		// Act
		RsData<Void> rsData = memberService.deleteMember();

		// Assert
		assertTrue(rsData.isFail());
		assertEquals("F-3", rsData.getCode());
		assertEquals("로그인이 필요합니다.", rsData.getMsg());
		assertNull(rsData.getData());
		verify(memberRepository, never()).delete(any(Member.class));
	}


	@Test
	@DisplayName("회원 정보 조회 - 비로그인 회원")
	void t009() {
		// Arrange
		when(rq.getMember()).thenReturn(null);

		// Act
		RsData<Member> rsData = memberService.getMemberInfo();

		// Assert
		assertTrue(rsData.isFail());
		assertEquals("F-4", rsData.getCode());
		assertEquals("로그인이 필요합니다.", rsData.getMsg());
		assertNull(rsData.getData());
	}

}
