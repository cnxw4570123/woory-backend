package com.woory.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentRequestDto {
	private Long groupId;
	private Long topicId;
	private String contentText;
	private String images;
}
