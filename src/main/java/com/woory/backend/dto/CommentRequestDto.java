package com.woory.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    private Long parentCommentId;
    private Long contentId;
    private Long userId;
    private String commentText;
    private Date commentDate;
}
