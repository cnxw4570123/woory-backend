package com.woory.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.woory.backend.entity.Comment;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDto {

    private Long commentId;
    private Long parentCommentId;
    private Long contentId;
    private Long userId;
    private String commentText;
    private String status;
    private Date commentDate;


    public static CommentDto fromComment(Comment comment){
        return CommentDto.builder()
            .commentId(comment.getCommentId())
            .contentId(comment.getContent().getContentId())
            .userId(comment.getUsers().getUserId())
            .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null)
            .commentText(comment.getCommentText())
            .commentDate(comment.getCommentDate())
            .build();
    }
}
