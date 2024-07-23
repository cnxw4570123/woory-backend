package com.woory.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyDto {
    private String profileUrl;
    private String name;
    private long userId;
    private boolean isEdit;
    private String comment;
    private long commentId;
}
