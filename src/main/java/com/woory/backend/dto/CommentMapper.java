package com.woory.backend.dto;

import com.woory.backend.entity.Comment;

import java.util.stream.Collectors;

public class CommentMapper {

    public static CommentReplyDto toDTO(Comment comment, boolean isEdit) {
        return CommentReplyDto.builder()
                .userId(comment.getUsers().getUserId())
                .profileUrl(comment.getUsers().getProfileImage()) // Assuming User has a getProfileUrl() method
                .name(comment.getUsers().getNickname()) // Assuming User has a getName() method
                .isEdit(isEdit) // Set isEdit value from service layer
                .commentId(comment.getCommentId())
                .comment(comment.getCommentText())
                .replies(comment.getReplies().stream()
                        .map(reply -> toReplyDTO(reply, isEdit))
                        .collect(Collectors.toList()))
                .build();
    }

    public static ReplyDto toReplyDTO(Comment reply, boolean isEdit) {
        return ReplyDto.builder()
                .userId(reply.getUsers().getUserId())
                .profileUrl(reply.getUsers().getProfileImage()) // Assuming User has a getProfileUrl() method
                .name(reply.getUsers().getNickname()) // Assuming User has a getName() method
                .isEdit(isEdit) // Set isEdit value from service layer
                .commentId(reply.getCommentId())
                .comment(reply.getCommentText())
                .build();
    }
}
