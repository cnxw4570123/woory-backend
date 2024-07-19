package com.woory.backend.dto;

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
}
