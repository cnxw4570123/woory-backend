package com.woory.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "content_reactions")
@Getter
@Setter
@NoArgsConstructor
public class ContentReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction", nullable = false)
    private ReactionType reaction;

    public ContentReaction(Content content, User user, ReactionType reaction) {
        this.content = content;
        this.user = user;
        this.reaction = reaction;
    }
}

