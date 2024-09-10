package com.woory.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.woory.backend.entity.TopicSet;

public interface TopicSetRepository extends JpaRepository<TopicSet, Long> {
	Optional<TopicSet> findTopicSetById(Long id);

	@Query(value = "select t.* from topic_set t order by RAND() LIMIT 1", nativeQuery = true)
	TopicSet findRandomTopic();
}
