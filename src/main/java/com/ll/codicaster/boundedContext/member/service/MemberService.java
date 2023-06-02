package com.ll.codicaster.boundedContext.member.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.member.entity.Member;
import com.ll.codicaster.boundedContext.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 아래 메서드들이 전부 readonly 라는 것을 명시, 나중을 위해
public class MemberService {
	private final PasswordEncoder passwordEncoder;

	private final MemberRepository memberRepository;
	public Member updateMemberInfo;

	public Optional<Member> findByUsername(String username) {
		return memberRepository.findByUsername(username);
	}

	@Transactional // SELECT 이외의 쿼리에 대한 가능성이 아주 조금이라도 있으면 붙인다.
	// 일반 회원가입(소셜 로그인을 통한 회원가입이 아님)
	public RsData<Member> join(String username, String password) {
		// "CODYCASTER" 해당 회원이 일반회원가입으로 인해 생성되었다는걸 나타내기 위해서
		return join("CODYCASTER", username, password);
	}

	// 내부 처리함수, 일반회원가입, 소셜로그인을 통한 회원가입(최초 로그인 시 한번만 발생)에서 이 함수를 사용함
	private RsData<Member> join(String providerTypeCode, String username, String password) {
		if (findByUsername(username).isPresent()) {
			return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username));
		}

		// 소셜 로그인을 통한 회원가입에서는 비번이 없다.
		if (StringUtils.hasText(password)) {
			password = passwordEncoder.encode(password);
		}

		Member member = Member
			.builder()
			.providerTypeCode(providerTypeCode)
			.username(username)
			.password(password)
			.build();

		memberRepository.save(member);

		return RsData.of("S-1", "회원가입이 완료되었습니다.", member);
	}

	// 소셜 로그인(카카오, 구글, 네이버) 로그인이 될 때 마다 실행되는 함수
	@Transactional
	public RsData<Member> whenSocialLogin(String providerTypeCode, String username) {
		Optional<Member> opMember = findByUsername(username);

		if (opMember.isPresent())
			return RsData.of("S-2", "로그인 되었습니다.", opMember.get());

		// 소셜 로그인를 통한 가입시 비번은 없다.
		return join(providerTypeCode, username, ""); // 최초 로그인 시 딱 한번 실행
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
	public void updateMemberInfo(Long memberId, String nickname, String bodytype, String gender) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. ID=" + memberId));

		member.updateInfo(nickname, bodytype, gender);
	}

}
