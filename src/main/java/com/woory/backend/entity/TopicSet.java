package com.woory.backend.entity;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Immutable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TopicSet {
	@Id
	@Column(name = "TOPIC_ID")
	private Long id;
	private String category;
	private String value;
	private Integer topic_byte;

}
