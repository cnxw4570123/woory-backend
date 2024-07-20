package com.woory.backend.dto;

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
    private Long userId;
    private ReactionType reaction;

    public static ContentReactionDto toContentReactionDto(ContentReaction reaction) {
        ContentReactionDto dto = new ContentReactionDto();
        dto.setContentId(reaction.getContent().getContentId());
        dto.setUserId(reaction.getUser().getUserId());
        dto.setReaction(reaction.getReaction());
        return dto;
    }
}
