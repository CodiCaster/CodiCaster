package com.ll.codicaster.boundedContext.notification.entity;

import java.time.LocalDateTime;

import com.ll.codicaster.base.baseEntity.BaseEntity;
import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.member.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Notification extends BaseEntity {

	private LocalDateTime readDate;
	private String typeCode;

	//좋아요와 팔로우 알림의 알림 수신자
	@ManyToOne
	private Member receiver;
	//좋아요 누른 사람, 팔로우 한사람
	@ManyToOne
	private Member actor;
	@ManyToOne
	private Article article;

	public boolean isRead() {
		return readDate != null;
	}

	public void markAsRead() {
		readDate = LocalDateTime.now();
	}

	public boolean isHot() {
		// 만들어진지 60분이 안되었다면 hot 으로 설정
		return getCreateDate().isAfter(LocalDateTime.now().minusMinutes(60));
	}

}
