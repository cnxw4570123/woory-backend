package com.woory.backend.dto;

import com.woory.backend.entity.ReactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentReactionDto {

    private Long commentId;
    private Long userId;
    private ReactionType reaction;
    private int reactionCount;
}
