package com.woory.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContentDto {
	private Long contentId;
	private String contentText;
	private String contentImgPath;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	private Date contentRegDate;
	private boolean IsFavorite;

	public ContentDto(String contentImgPath, Date contentRegDate, boolean isFavorite) {
		this.contentImgPath = contentImgPath;
		this.contentRegDate = contentRegDate;
		IsFavorite = isFavorite;
	}
}
