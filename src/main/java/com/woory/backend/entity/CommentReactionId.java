package com.woory.backend.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class CommentReactionId implements Serializable {

    private Long commentId;
    private Long userId;

    public CommentReactionId() {
    }

    public CommentReactionId(Long commentId, Long userId) {
        this.commentId = commentId;
        this.userId = userId;
    }
}
