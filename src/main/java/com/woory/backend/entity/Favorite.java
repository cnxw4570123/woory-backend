package com.woory.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "heart_id")
	private Long id;
	@ManyToOne
	private GroupUser groupUser;

	@ManyToOne
	private Topic topic;

	private Long groupId;

	private String contentImage;
}
