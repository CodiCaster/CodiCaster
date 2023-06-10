package com.ll.codicaster.boundedContext.notification;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

	private final NotificationRepository notificationRepository;

	public List<Notification> findByReceiver(Member author) {
		return notificationRepository.findByArticleAuthor(author);
	}

	@Transactional
	public RsData<Notification> makeLike(Article article, Member liker) {
		return make(article, "LIKE", liker);
	}

	private RsData<Notification> make(Article article, String typeCode, Member actor) {
		Notification notification = Notification
			.builder()
			.article(article)
			.typeCode(typeCode)
			.actor(actor)
			.createDate(LocalDateTime.now())
			.receiver(article.getAuthor())
			.build();
		notificationRepository.save(notification);
		return RsData.of("S-1", "알림 메세지가 생성되었습니다.", notification);
	}

	@Transactional
	public RsData markAsRead(List<Notification> notifications) {
		notifications
			.stream()
			.filter(notification -> !notification.isRead())
			.forEach(Notification::markAsRead);

		return RsData.of("S-1", "읽음 처리 되었습니다.");
	}

	public boolean countUnreadNotificationsByAuthor(Member receiver) {
		return notificationRepository.countByReceiverAndReadDateIsNull(receiver) > 0;
	}

}
