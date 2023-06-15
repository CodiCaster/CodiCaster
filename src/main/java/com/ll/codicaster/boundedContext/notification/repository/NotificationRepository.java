package com.ll.codicaster.boundedContext.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.member.entity.Member;
import com.ll.codicaster.boundedContext.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByArticleAuthor(Member author);

	int countByReceiverAndReadDateIsNull(Member receiver);

	List<Notification> findByReceiver(Member receiver);

	List<Notification> findByArticleIdAndActor(Long articleId, Member actor);

	List<Notification> findByArticleId(Long articleId);
<<<<<<< HEAD

=======
>>>>>>> ed69c83 ([fix] 좋아요 표시 되어있는 게시물 삭제 가능)
}
