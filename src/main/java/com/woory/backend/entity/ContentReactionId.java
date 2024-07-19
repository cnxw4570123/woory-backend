package com.woory.backend.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ContentReactionId implements Serializable {

    @Column(name = "content_id")
    private Long contentId;

    @Column(name = "user_id")
    private Long userId;

    public ContentReactionId() {
    }

    public ContentReactionId(Long contentId, Long userId) {
        this.contentId = contentId;
        this.userId = userId;
    }


}
