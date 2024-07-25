package com.woory.backend.repository;

import com.woory.backend.entity.Topic;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long>, BatchTopicRepository {
	Optional<Topic> findByTopicId(Long topicId);

	@Query("select t from Topic t where t.group.groupId = :groupId and t.issueDate = :date")
	Optional<Topic> findTopicByGroupIdAndIssueDate(@Param("groupId") Long groupId, @Param("date") LocalDate start);

	@Query("select t from Topic t where t.group.groupId = :groupId and t.issueDate = :date")
	@EntityGraph(attributePaths = {"content", "content.users"})
	Optional<Topic> findTopicByGroupIdAndIssueDateWithContent(@Param("groupId") Long groupId,
		@Param("date") LocalDate start);

	boolean existsByGroup_GroupIdAndAndIssueDate(long groupId, LocalDate date);

	@Query("select t.topicId from Topic t where t.group.groupId = :groupId and t.issueDate = :date")
	Optional<Long> findTopicIdByRegDateAndGroupId(@Param("groupId") Long groupId, @Param("date") Date date);
}