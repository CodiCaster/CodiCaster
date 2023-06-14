package com.ll.codicaster.boundedContext.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.codicaster.boundedContext.member.entity.Member;
import com.ll.codicaster.boundedContext.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByArticleAuthor(Member author);

	int countByReceiverAndReadDateIsNull(Member receiver);

	List<Notification> findByReceiver(Member receiver);

	List<Notification> findByArticleIdAndActor(Long articleId, Member actor);
}
