package com.woory.backend.repository;

import com.woory.backend.entity.Topic;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long>, BatchTopicRepository {
	Optional<Topic> findByTopicId(Long topicId);

	@Query("select t from Topic t where t.group.groupId = :groupId and t.issueDate between :date and :end")
	Optional<Topic> findTopicByGroupIdAndIssueDate(@Param("groupId") Long groupId, @Param("date") LocalDate start,
		@Param("end") LocalDate end);

	@Query("select t from Topic t where t.group.groupId = :groupId and t.issueDate = :date")
	@EntityGraph(attributePaths = {"content", "content.users"})
	Optional<Topic> findTopicByGroupIdAndIssueDateWithContent(@Param("groupId") Long groupId,
		@Param("date") LocalDate start);


	// @Query(value = "SELECT * FROM TOPIC T LEFT JOIN CONTENT C ON T.TOPIC_ID = C.TOPIC_ID WHERE T.GROUP_ID = :groupId AND T.ISSUE_DATE = :DATE", nativeQuery = true)
}