package com.woory.backend.dto;

import com.woory.backend.entity.Comment;

import java.util.stream.Collectors;

public class CommentMapper {

    public static CommentReplyDto toDTO(Comment comment, Long userId) {
        return CommentReplyDto.builder()
                .isEdit(comment.getUsers().getUserId().equals(userId)) // Set isEdit value from service layer
                .userId(comment.getUsers().getUserId())
                .profileUrl(comment.getUsers().getProfileImage()) // Assuming User has a getProfileUrl() method
                .name(comment.getUsers().getNickname()) // Assuming User has a getName() method
                .commentId(comment.getCommentId())
                .comment(comment.getCommentText())
                .replies(comment.getReplies().stream()
                        .map(reply -> toReplyDTO(reply, userId))
                        .collect(Collectors.toList()))
                .build();
    }

    public static ReplyDto toReplyDTO(Comment reply, Long userId) {
        return ReplyDto.builder()
                .isEdit(reply.getUsers().getUserId().equals(userId)) // Set isEdit value from service layer
                .userId(reply.getUsers().getUserId())
                .profileUrl(reply.getUsers().getProfileImage()) // Assuming User has a getProfileUrl() method
                .name(reply.getUsers().getNickname()) // Assuming User has a getName() method
                .commentId(reply.getCommentId())
                .comment(reply.getCommentText())
                .build();
    }
}
