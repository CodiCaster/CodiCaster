package com.ll.codicaster.base.initData;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import com.ll.codicaster.boundedContext.member.entity.Member;
import com.ll.codicaster.boundedContext.member.service.MemberService;

@Configuration
@Profile({"dev", "test"})
public class NotProd {
	@Value("${custom.security.oauth2.client.registration.kakao.devUserOauthId}")
	private String kakaoDevUserOAuthId;

	@Value("${custom.security.oauth2.client.registration.naver.devUserOauthId}")
	private String naverDevUserOAuthId;

	@Bean
	CommandLineRunner initData(
		MemberService memberService
	) {
		return new CommandLineRunner() {
			@Override
			@Transactional
			public void run(String... args) throws Exception {
				Member memberAdmin = memberService.join("admin", "1234").getData();
				Member memberUser1 = memberService.join("user1", "1234").getData();
				Member memberUserByKakao = memberService.whenSocialLogin("KAKAO",
					"KAKAO__%s".formatted(kakaoDevUserOAuthId)).getData();
				Member memberUserByNaver = memberService.whenSocialLogin("NAVER",
					"NAVER__%s".formatted(naverDevUserOAuthId)).getData();
			}
		};
	}
}
