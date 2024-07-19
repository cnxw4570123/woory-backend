package com.woory.backend.repository2;

import com.woory.backend.entity.ContentReaction;
import com.woory.backend.entity.ContentReactionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContentReactionRepository extends JpaRepository<ContentReaction, ContentReactionId> {

    List<ContentReaction> findByContent_ContentId(Long ContentId);
}
