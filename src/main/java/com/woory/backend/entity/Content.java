package com.woory.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topicId")
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private User users;


}


