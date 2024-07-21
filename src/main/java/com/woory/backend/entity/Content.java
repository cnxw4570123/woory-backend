package com.woory.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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
	private Long contentId;
	private String contentText;
	private String contentImgPath;
	private Date contentRegDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "topicId")
	private Topic topic;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userid")
	private User users;

	@OneToMany(mappedBy = "content", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference  // content 필드를 무시하여 순환 참조 방지
	private List<Comment> comments;

	@Builder.Default
	@OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ContentReaction> contentReactions = new ArrayList<>();
}


