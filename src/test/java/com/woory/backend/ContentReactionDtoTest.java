package com.woory.backend;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.woory.backend.dto.ContentReactionDto;
import com.woory.backend.entity.Content;
import com.woory.backend.entity.ContentReaction;
import com.woory.backend.entity.ReactionType;
import com.woory.backend.entity.User;

public class ContentReactionDtoTest {

	@Test
	void 반응별_반환_테스트() {
		//given
		Content content = new Content();
		content.setContentId(1L);

		User user1 = new User();
		user1.setUserId(1L);

		User user2 = new User();
		user2.setUserId(2L);

		User user3 = new User();
		user3.setUserId(3L);

		List<ContentReaction> contentReactions = List.of(new ContentReaction(content, user1, ReactionType.LIKE),
			new ContentReaction(content, user2, ReactionType.LIKE),
			new ContentReaction(content, user3, ReactionType.SAD));

		// when
		Map<ReactionType, List<ContentReactionDto>> separatedReactions =
			ContentReactionDto.toSeparatedReactions(contentReactions);

		// then
		Assertions.assertEquals(separatedReactions.get(ReactionType.LIKE).size(), 2);
		Assertions.assertEquals(separatedReactions.get(ReactionType.SAD).size(), 1);
	}
}
