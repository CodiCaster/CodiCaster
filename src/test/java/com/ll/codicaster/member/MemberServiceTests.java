package com.ll.codicaster.member;

import com.ll.codicaster.boundedContext.member.entity.Member;
import com.ll.codicaster.boundedContext.member.repository.MemberRepository;
import com.ll.codicaster.boundedContext.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class MemberServiceTests {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private MemberService memberService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("회원 탈퇴")
	void t001() {
		// GIVEN
		Long memberId = 1L;
		Member existingMember = Member.builder()
			.id(memberId)
			.build();

		when(memberRepository.findById(memberId)).thenReturn(Optional.of(existingMember));

		// WHEN
		memberService.deleteMember(memberId);

		// THEN
		verify(memberRepository, times(1)).findById(memberId);
		verify(memberRepository, times(1)).delete(existingMember);
	}
}
