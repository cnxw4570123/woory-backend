package com.woory.backend.repository2;

import com.woory.backend.entity.Comment;
import com.woory.backend.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByCommentId(Long commentId);
    List<Comment> findByContent_ContentIdAndStatus(Long contentId, String status);
}
