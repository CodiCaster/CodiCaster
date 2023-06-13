package com.ll.codicaster.boundedContext.member.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.member.entity.Member;
import com.ll.codicaster.boundedContext.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;
	private final Rq rq;

	public Optional<Member> findByUsername(String username) {
		return memberRepository.findByUsername(username);
	}

	@Transactional
	public RsData<Member> join(String username, String password) {
		return join("CODYCASTER", username, password);
	}

	private RsData<Member> join(String providerTypeCode, String username, String password) {
		if (findByUsername(username).isPresent()) {
			return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username));
		}

		if (StringUtils.hasText(password)) {
			password = passwordEncoder.encode(password);
		}

		Member member = Member.builder()
			.providerTypeCode(providerTypeCode)
			.username(username)
			.password(password)
			.build();

		memberRepository.save(member);

		return RsData.of("S-1", "회원가입이 완료되었습니다.", member);
	}

	@Transactional
	public RsData<Member> whenSocialLogin(String providerTypeCode, String username) {
		Optional<Member> opMember = findByUsername(username);

		return opMember.map(member -> RsData.of("S-2", "로그인 되었습니다.", member))
			.orElseGet(() -> join(providerTypeCode, username, ""));

	}

	@Transactional(readOnly = true)
	public RsData<String> checkNickname(String nickname) {
		Optional<Member> optionalMember = memberRepository.findByNickname(nickname);

		if (optionalMember.isPresent()) {
			return RsData.of("F-1", "해당 닉네임(%s)은 이미 사용중입니다.".formatted(nickname));
		}

		return RsData.of("S-1", "사용 가능한 닉네임입니다.");
	}

	@Transactional
	public RsData<Void> updateMemberInfo(String nickname, int bodyType, String gender) {
		Member member = rq.getMember();

		if (member == null) {
			return RsData.of("F-2", "로그인이 필요합니다.");
		}

		member.updateInfo(nickname, bodyType, gender);
		return RsData.of("S-2", "회원 정보가 업데이트되었습니다.", null);
	}

	@Transactional
	public RsData<Void> deleteMember() {
		Member member = rq.getMember();

		if (member == null) {
			return RsData.of("F-3", "로그인이 필요합니다.");
		}

		memberRepository.delete(member);
		return RsData.of("S-3", "회원 탈퇴가 완료되었습니다.", null);
	}

	@Transactional(readOnly = true)
	public RsData<Member> getMemberInfo() {
		Member member = rq.getMember();

		if (member == null) {
			return RsData.of("F-4", "로그인이 필요합니다.");
		}

		return RsData.successOf(member);
	}
}
