package com.woory.backend.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.amazonaws.transform.MapEntry;
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

	public static List<ForStatistics> toReactionForStatistics(Long userId, List<ContentReaction> reactions) {
		Map<ReactionType, Set<Long>> reactionTypeSetMap = new HashMap<>();

		for (ContentReaction cr : reactions) {
			Set<Long> longs = reactionTypeSetMap.putIfAbsent(cr.getReaction(),
				new HashSet<>(Set.of(cr.getUser().getUserId())));
			if (longs == null) {
				continue;
			}
			longs.add(cr.getUser().getUserId());
		}
		List<ForStatistics> ret = new ArrayList<>();
		for (Map.Entry<ReactionType, Set<Long>> entry : reactionTypeSetMap.entrySet()) {
			Set<Long> value = entry.getValue();
			ReactionType key = entry.getKey();
			boolean isActive = value.contains(userId);
			int size = value.size();
			ret.add(new ForStatistics(key, size, isActive));
		}
		return ret;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ForStatistics {
		private ReactionType reactionType;
		private long count;
		private boolean IsActive;
	}
}