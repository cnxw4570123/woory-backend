package com.woory.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.woory.backend.entity.ContentReaction;

public interface ContentReactionRepository extends JpaRepository<ContentReaction, Long> {
	List<ContentReaction> findByContent_ContentId(Long contentId);

	@Query("select cr from ContentReaction cr join fetch cr.user u where cr.content.contentId = :contentId")
	List<ContentReaction> findByContentIdWithUser(@Param("contentId") Long contentId);

	Optional<ContentReaction> findContentReactionByContent_ContentIdAndUser_UserId(Long contentId, Long userId);
}
