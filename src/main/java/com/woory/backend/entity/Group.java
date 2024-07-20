package com.woory.backend.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "group_table")
@Getter
@Setter
public class Group {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "groupId")
	private Long groupId;

	@Column(name = "groupName")
	private String groupName;

	@Column(name = "photoPath")
	private String photoPath;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User users;

	@OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private List<Topic> topic = new ArrayList<>();
}
