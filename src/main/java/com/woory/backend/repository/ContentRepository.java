package com.woory.backend.repository;

import com.woory.backend.dto.ContentMonthDto;
import com.woory.backend.entity.Content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
	@Query("select c from Content c join fetch c.users where c.contentId = :contentId")
	Optional<Content> findContentWithUserByContentId(@Param("contentId") Long contentId);

	Optional<Content> findByContentId(Long contentId);

	boolean existsByTopic_TopicIdAndUsers_UserId(Long topicId, Long userId);

	@Query("select c from Content c join fetch c.topic where c.contentId = :contentId")
	Optional<Content> findContentWithTopic(@Param("contentId") Long contentId);

	@Query(value = "SELECT c.topic_id, c.content_img_path, " +
		"DATE_FORMAT(c.content_reg_date, '%Y-%m-%d') AS content_reg_date, " +
		"if(c.topic_id in (select f.topic_id from favorite f join group_user gu on gu.id = f.group_user_id where gu.user_id = :userId), 'true', 'false') as isFavorite "+
		"FROM content c " +
		"JOIN topic t ON c.topic_id = t.topic_id " +
		"JOIN group_table g ON t.group_id = g.group_id " +
		"WHERE g.group_id = :groupId " +
		"AND DATE_FORMAT(c.content_reg_date, '%Y-%m') LIKE CONCAT(:date, '%') " +
		"ORDER BY c.content_reg_date ASC", nativeQuery = true)
	List<ContentMonthDto> findByDateWithImgPath(@Param("groupId") Long groupId, @Param("userId") Long userId,
		@Param("date") String date);

	List<Content> findByTopic_Group_GroupId(Long groupId);
}
