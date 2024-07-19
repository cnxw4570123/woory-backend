package com.woory.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "content")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contentId")
    private Long contentId;

    @Column(name = "contentText", nullable = true)
    private String contentText;

    @Column(name = "contentImgPath", nullable = true)
    private String contentImgPath;

    @Column(name = "contentRegDate")
    private Date contentRegDate;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topicId")
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private User users;

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference  // content 필드를 무시하여 순환 참조 방지
    private List<Comment> comments;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContentReaction> contentReactions = new HashSet<>();
}


