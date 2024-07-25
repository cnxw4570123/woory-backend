package com.woory.backend.dto;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.woory.backend.entity.Topic;
import com.woory.backend.entity.TopicSet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopicDto {
	private String topicContent;
	private Long topicId;
	private Date issueDate;
	private Long groupId;
	private int topicByte;
	private boolean hasPrevDay;
	private boolean hasNextDay;
	private boolean IsPosted;
	private List<ContentWithUserDto> contents;

	public static TopicDto fromTopicOnly(Topic topic) {
		return TopicDto.builder()
			.topicContent(topic.getTopicContent())
			.topicByte(topic.getTopicByte())
			.topicId(topic.getTopicId())
			.issueDate(topic.getIssueDate())
			.contents(Collections.emptyList())
			.build();
	}

	public static TopicDto fromTopicSetWithGroupIdAndDate(TopicSet topicSet, Date date, long groupId) {
		return TopicDto.builder()
			.topicContent(topicSet.getValue())
			.topicByte(topicSet.getTopic_byte())
			.issueDate(date)
			.groupId(groupId)
			.build();
	}

	public static TopicDto fromTopicWithContent(Long userId, Topic topic, boolean hasPrevDay, boolean hasNextDay) {
		TopicDtoBuilder contents1 = TopicDto.builder()
			.topicContent(topic.getTopicContent())
			.topicId(topic.getTopicId())
			.topicByte(topic.getTopicByte())
			.issueDate(topic.getIssueDate())
			.hasNextDay(hasNextDay)
			.hasPrevDay(hasPrevDay)
			.contents(
				topic.getContent().stream()
					.map(c -> ContentWithUserDto.toContentWithUserDto(userId, c))
					.toList()
			);

		return contents1.IsPosted(contents1.contents.stream()
				.anyMatch(ContentWithUserDto::isIsEdit))
			.build();
	}

	public static TopicDto fromTopic(Topic topic) {
		return TopicDto.builder()
			.topicContent(topic.getTopicContent())
			.topicByte(topic.getTopicByte())
			.topicId(topic.getTopicId())
			.issueDate(topic.getIssueDate())
			.build();
	}

}
