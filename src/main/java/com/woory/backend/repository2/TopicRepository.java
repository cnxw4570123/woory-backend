package com.woory.backend.repository2;

import com.woory.backend.entity.Group;
import com.woory.backend.entity.Topic;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long>, BatchTopicRepository {
	Optional<Topic> findByTopicId(Long topicId);
}
