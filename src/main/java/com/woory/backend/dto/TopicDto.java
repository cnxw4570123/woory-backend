package com.woory.backend.dto;

import java.util.Date;

import com.woory.backend.entity.TopicSet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDto {
	private String topicContent;
	private Date issueDate;
	private Long groupId;
	private int topicByte;

	public static TopicDto fromTopicSetWithGroupIdAndDate(TopicSet topicSet, Date date, long groupId) {
		return TopicDto.builder()
			.topicContent(topicSet.getValue())
			.topicByte(topicSet.getTopic_byte())
			.issueDate(date)
			.groupId(groupId)
			.build();
	}
}
