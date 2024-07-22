package com.woory.backend.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.woory.backend.entity.ContentReaction;
import com.woory.backend.entity.ReactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentReactionDto {

	private Long contentId;
	//groupUser로 변경 필요 String group
	private Long userId;
	private ReactionType reaction;

	public static ContentReactionDto toContentReactionDto(ContentReaction reaction) {
		return ContentReactionDto.builder()
			.contentId(reaction.getContent().getContentId())
			.userId(reaction.getUser().getUserId())
			.reaction(reaction.getReaction())
			.build();
	}

	public static Map<ReactionType, List<ContentReactionDto>> toSeparatedReactions(List<ContentReaction> reactions) {
		return reactions.stream()
			.map(ContentReactionDto::toContentReactionDto)
			.collect(Collectors.groupingBy(ContentReactionDto::getReaction));
	}

	public static Map<ReactionType, Long> toReactionSizeByStatus(List<ContentReaction> reactions) {
		return reactions.stream()
			.map(ContentReactionDto::toContentReactionDto)
			.collect(Collectors.groupingBy(ContentReactionDto::getReaction, Collectors.counting()));
	}
}
