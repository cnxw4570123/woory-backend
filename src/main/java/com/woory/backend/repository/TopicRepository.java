package com.woory.backend.repository;

import com.woory.backend.entity.Topic;

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
}
