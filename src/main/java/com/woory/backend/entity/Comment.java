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

	@Builder.Default
	@OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<Comment> replies = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "content_id")
	private Content content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User users;

	@Column(name = "comment_text", length = 255, nullable = false)
	private String commentText;

	@Column(name = "comment_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date commentDate;

	public void addReply(Comment reply) {
		replies.add(reply);
		reply.setParentComment(this);
	}

	public void removeReply(Comment reply) {
		replies.remove(reply);
		reply.setParentComment(null);
	}
}
