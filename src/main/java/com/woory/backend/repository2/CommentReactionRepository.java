package com.woory.backend.repository2;

import com.woory.backend.entity.CommentReaction;
import com.woory.backend.entity.CommentReactionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction, CommentReactionId> {

    List<CommentReaction> findByComment_CommentId(Long commentId);

    List<CommentReaction> findByUser_UserId(Long userId);
}