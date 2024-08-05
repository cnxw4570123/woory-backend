package com.woory.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.woory.backend.dto.NotificationDto;
import com.woory.backend.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	@Query(
		"select n, "
			+ "case when n.contentUserId is not null then (select u.username from User u where u.userId = n.contentUserId)"
			+ "when n.commentUserId is not null then (select u.username from User u where u.userId = n.commentUserId) "
			+ "when n.replyUserId is not null then (select u.username from User u where u.userId = n.replyUserId) "
			+ "when n.reactionUserId is not null then (select u.username from User u where u.userId = n.reactionUserId) "
			+ "else null "
			+ "end as author "
			+ "from Notification n "
			+ "where (n.notificationType in (com.woory.backend.entity.NotificationType.CONTENT, com.woory.backend.entity.NotificationType.TOPIC) and n.groupId = :groupId) "
			+ "or n.userId = :userId "
			+ "order by n.issueDate desc "
			+ "limit 10")
	List<NotificationDto> findAllByUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);
}
