package com.ll.codicaster.boundedContext.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.codicaster.boundedContext.member.entity.Member;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByArticleAuthor(Member author);

	int countByReceiverAndReadDateIsNull(Member receiver);
}
