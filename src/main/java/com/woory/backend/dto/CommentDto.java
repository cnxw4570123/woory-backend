package com.woory.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long commentId;
    private Long parentCommentId;
    private Long contentId;
    private Long userId;
    private String commentText;
    private String status;
    private Date commentDate;

}
