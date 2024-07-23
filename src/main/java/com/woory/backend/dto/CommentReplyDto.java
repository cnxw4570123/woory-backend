package com.woory.backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReplyDto {
    private String profileUrl;
    private String name;
    private long userId;
    private long commentId;
    private boolean isEdit;
    private String comment;
    private List<ReplyDto> replies;
}
