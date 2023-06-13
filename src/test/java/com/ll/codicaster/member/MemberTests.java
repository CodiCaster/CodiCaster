package com.ll.codicaster.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.ll.codicaster.boundedContext.member.entity.Member;

public class MemberTests {
	private Member member;

	@BeforeEach
	public void setUp() {
		Map<String, Integer> tagMap = new HashMap<>();
		tagMap.put("tag1", 5);
		tagMap.put("tag2", 5);
		tagMap.put("tag3", 3);

		member = Member.builder()
			.providerTypeCode("CODYCASTER")
			.username("testUser")
			.password("testPassword")
			.nickname("testNickname")
			.bodyType(3)
			.gender("Male")
			.tagMap(tagMap)
			.build();
	}

	@Test
	@DisplayName("권한 확인 테스트")
	public void t001() {
		List<? extends GrantedAuthority> authorities = member.getGrantedAuthorities();

		// 모든 회원은 "member" 권한을 가집니다.
		assertTrue(authorities.contains(new SimpleGrantedAuthority("member")));

		// "admin" 사용자는 "admin" 권한도 가집니다.
		if (member.isAdmin()) {
			assertTrue(authorities.contains(new SimpleGrantedAuthority("admin")));
		}
	}

	@Test
	@DisplayName("관리자 확인 테스트")
	public void t002() {
		assertEquals("admin".equals(member.getUsername()), member.isAdmin());
	}

	@Test
	@DisplayName("멤버 정보 업데이트")
	public void t003() {
		member.updateInfo("newNickname", 2, "Female");

		assertEquals("newNickname", member.getNickname());
		assertEquals(2, member.getBodyType());
		assertEquals("Female", member.getGender());
	}

	@Test
	@DisplayName("가장 많이 사용한 태그 확인")
	public void t004() {
		List<String> expected = Arrays.asList("tag1", "tag2");
		List<String> mostUsedTags = member.getMostUsedTags();

		assertEquals(expected, mostUsedTags);
	}

	@Test
	@DisplayName("체질에 따른 표시 이름 확인")
	public void t005() {
		String expected = "보통";
		String actual = member.getBodyTypeDisplayName();

		assertEquals(expected, actual);
	}
}

