package com.woory.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "topic")
public class Topic {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "topicId")
	private Long topicId;

	@Column(name = "topicContent")
	private String topicContent;

	@Column(name = "issueDate")
	private Date issueDate;

	private int topicByte;

	@ManyToOne
	@JoinColumn(name = "group_id")
	private Group group;

	@Builder.Default
	@OneToMany(mappedBy = "topic", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Content> content = new ArrayList<>();
}
