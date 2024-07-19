package com.woory.backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User users;

    @Column(name = "comment_text", length = 255, nullable = false)
    private String commentText;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "love_count", nullable = false)
    private int loveCount;

    @Column(name = "wow_count", nullable = false)
    private int wowCount;

    @Column(name = "sad_count", nullable = false)
    private int sadCount;

    @Column(name = "angry_count", nullable = false)
    private int angryCount;

    @Column(name = "comment_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date commentDate;



}
