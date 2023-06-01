package com.ll.codicaster.boundedContext.member.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.ll.codicaster.base.baseEntity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Member extends BaseEntity {
	private String providerTypeCode; // 일반회원인지, 카카오로 가입한 회원인지, 네이버로 가입한 회원인지

	@Column(unique = true)
	private String username;

	private String password;

	@Column(unique = true)
	private String nickname;

	private String bodytype;
	// 이 함수 자체는 만들어야 한다. 스프링 시큐리티 규격
	public List<? extends GrantedAuthority> getGrantedAuthorities() {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

		// 모든 멤버는 member 권한을 가진다.
		grantedAuthorities.add(new SimpleGrantedAuthority("member"));

		// username이 admin인 회원은 추가로 admin 권한도 가진다.
		if (isAdmin()) {
			grantedAuthorities.add(new SimpleGrantedAuthority("admin"));
		}

		return grantedAuthorities;
	}

	public boolean isAdmin() {
		return "admin".equals(username);
	}

	public void updateInfo(String nickname, String bodytype) {
		this.nickname = nickname;
		this.bodytype = bodytype;
	}

	// //유저가 가장 많이 이용한 태그
	// public String getMostUsedTag() {
	// 	String mostUsedTag = null;
	// 	int maxCount = 0;
	//
	// 	for (Map.Entry<String, Integer> entry : tagMap.entrySet()) {
	// 		if (entry.getValue() > maxCount) {
	// 			mostUsedTag = entry.getKey();
	// 			maxCount = entry.getValue();
	// 		}
	// 	}
	// 	return mostUsedTag;
	// }

}
