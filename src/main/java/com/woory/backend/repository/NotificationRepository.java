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
		value = "select *, "
			+ "case when content_user_id is not null then (select u.nickname from User u where u.user_id = n.content_user_id)"
			+ "when comment_user_id is not null then (select u.nickname from User u where u.user_id = n.comment_user_id) "
			+ "when reply_user_id is not null then (select u.nickname from User u where u.user_id = n.reply_user_id) "
			+ "when reaction_user_id is not null then (select u.nickname from User u where u.user_id = n.reaction_user_id) "
			+ "end as author "
			+ "from Notification n "
			+ "where (notification_type in ('CONTENT', 'TOPIC') and group_id = :groupId) "
			+ "or n.user_id = :userId "
			+ "order by issue_date desc, notification_id desc "
			+ "limit 10" , nativeQuery = true)
	List<NotificationDto> findAllByUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);
}
