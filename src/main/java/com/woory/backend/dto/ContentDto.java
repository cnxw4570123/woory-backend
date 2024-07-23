package com.woory.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.woory.backend.entity.Content;
import com.woory.backend.entity.ContentReaction;
import com.woory.backend.entity.ReactionType;

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
	private Date contentRegDate;
	private TopicRequestDto topic;
	private int commentsCount;
	private Map<ReactionType, Long> countByReaction;
  
  public ContentDto(Long contentId, String contentText, String contentImgPath, Date contentRegDate) {
      this.contentId = contentId;
      this.contentText = contentText;
      this.contentImgPath = contentImgPath;
      this.contentRegDate = contentRegDate;
  }

	public static ContentDto toContentDto(Content content) {
		return ContentDto.builder()
			.contentId(content.getContentId())
			.contentRegDate(content.getContentRegDate())
			.contentText(content.getContentText())
			.commentsCount(content.getComments().size())
			.contentImgPath(content.getContentImgPath())
			.countByReaction(ContentReactionDto.toReactionSizeByStatus(content.getContentReactions()))
			.build();
	}
}
