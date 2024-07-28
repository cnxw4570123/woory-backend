package com.woory.backend.repository;

import com.woory.backend.entity.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByCommentId(Long commentId);
    List<Comment> findByContent_ContentId(Long contentId);
    List<Comment> findByParentComment(Comment parentComment);
    void deleteByContent_ContentId(Long contentId);
    void deleteByContent_ContentIdAndUsers_UserId(Long contentId, Long userId);
}
