package com.ll.codicaster.boundedContext.member.entity;

import static jakarta.persistence.GenerationType.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.ll.codicaster.boundedContext.article.entity.Article;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKeyColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Member {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;
	private String providerTypeCode; // 카카오로 가입한 회원인지, 네이버로 가입한 회원인지
	@Column(unique = true)
	private String username;
	private String password;
	@Column(unique = true)
	private String nickname;

	@Column
	private int bodyType;

	private String gender;
	@ElementCollection
	@CollectionTable(name = "member_tagMap", joinColumns = @JoinColumn(name = "member_id"))
	@MapKeyColumn(name = "tag_type")
	@Column(name = "tag_count")
	private Map<String, Integer> tagMap;
	@ManyToMany(mappedBy = "likedMembers", cascade = CascadeType.REMOVE)
	private Set<Article> likedArticles = new HashSet<>();

	//태그 사용 횟수 확인 => 최대값 리스트 반환
	public static List<String> countUsedTags(List<String> mostUsedTags, int maxCount, Map<String, Integer> tagMap) {
		for (Map.Entry<String, Integer> entry : tagMap.entrySet()) {
			int count = entry.getValue();
			if (count > maxCount) {
				maxCount = count;
				mostUsedTags.clear();
				mostUsedTags.add(entry.getKey());
			} else if (count == maxCount) {
				mostUsedTags.add(entry.getKey());
			}
		}
		return mostUsedTags;
	}

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

	public void updateInfo(String nickname, int bodyType, String gender) {

		this.nickname = nickname;
		this.bodyType = bodyType;
		this.gender = gender;
	}

	//유저가 가장 많이 이용한 태그
	public List<String> getMostUsedTags() {
		List<String> mostUsedTags = new ArrayList<>();
		int maxCount = 0;

		Map<String, Integer> tagMap = this.tagMap;

		return countUsedTags(mostUsedTags, maxCount, tagMap);
	}

	public String getBodyTypeDisplayName() {
		return switch (bodyType) {
			case 1 -> "추위 많이 탐";
			case 2 -> "추위 조금 탐";
			case 3 -> "보통";
			case 4 -> "더위 조금 탐";
			default -> "더위 많이탐";
		};
	}

}
