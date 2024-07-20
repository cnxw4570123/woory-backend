package com.woory.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.woory.backend.entity.TopicSet;

public interface TopicSetRepository extends JpaRepository<TopicSet, Long> {
	Optional<TopicSet> findTopicSetById(Long id);
}
