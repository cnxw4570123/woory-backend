package com.woory.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.woory.backend.entity.ContentReaction;

public interface ContentReactionRepository extends JpaRepository<ContentReaction, Long> {
	List<ContentReaction> findByContent_ContentId(Long contentId);

	Optional<ContentReaction> findContentReactionByContent_ContentIdAndUser_UserId(Long contentId, Long userId);
}
