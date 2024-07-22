package com.woory.backend.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.woory.backend.entity.ContentReaction;
import com.woory.backend.entity.ReactionType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentReactionDto {

	private Long contentId;
	//groupUser로 변경 필요 String group
	private Long userId;
	private ReactionType reaction;


	public static ContentReactionDto toContentReactionDto(ContentReaction reaction) {
		ContentReactionDto dto = new ContentReactionDto();
		dto.setContentId(reaction.getContent().getContentId());
		dto.setUserId(reaction.getUser().getUserId());
		dto.setReaction(reaction.getReaction());
		return dto;
	}

	public static Map<ReactionType, List<ContentReactionDto>> toSeparatedReactions(List<ContentReaction> reactions) {
		return reactions.stream()
			.map(ContentReactionDto::toContentReactionDto)
			.collect(Collectors.groupingBy(ContentReactionDto::getReaction));
	}

}
