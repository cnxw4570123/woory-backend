package com.woory.backend.service;

import com.woory.backend.dto.CommentDto;
import com.woory.backend.dto.CommentReactionDto;
import com.woory.backend.dto.CommentRequestDto;
import com.woory.backend.entity.*;
import com.woory.backend.repository2.*;
import com.woory.backend.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private CommentRepository commentRepository;
    private ContentRepository contentRepository;
    private UserRepository userRepository;
    private GroupUserRepository groupUserRepository;
    private CommentReactionRepository commentReactionRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, ContentRepository contentRepository,
                          UserRepository userRepository, GroupUserRepository groupUserRepository,
                          CommentReactionRepository commentReactionRepository) {
        this.commentRepository = commentRepository;
        this.contentRepository = contentRepository;
        this.userRepository = userRepository;
        this.groupUserRepository = groupUserRepository;
        this.commentReactionRepository = commentReactionRepository;
    }

    @Transactional
    public CommentDto addComment(Long groupId, CommentRequestDto commentRequestDto) {
        Optional<GroupUser> byUserUserIdAndGroupGroupId = groupUserRepository.findByUser_UserIdAndGroup_GroupId(commentRequestDto.getUserId(), groupId);
        Optional<Content> contentOptional = contentRepository.findByContentId(commentRequestDto.getContentId());
        Optional<User> userOptional = userRepository.findByUserId(commentRequestDto.getUserId());
        GroupStatus status = byUserUserIdAndGroupGroupId
                .orElseThrow(() -> new NoSuchElementException("가족에서 확인되지 않는 유저입니다.")).getStatus();
        if (status == GroupStatus.BANNED || status == GroupStatus.NON_MEMBER) {
            throw new IllegalStateException("가족에서 확인되지 않는 유저입니다.");
        }
        if (contentOptional.isPresent() && userOptional.isPresent()) {
            Content content = contentOptional.get();
            User user = userOptional.get();

            Comment parentComment = null;
            if (commentRequestDto.getParentCommentId() != null) {
                parentComment = commentRepository.findByCommentId(commentRequestDto.getParentCommentId())
                        .orElse(null);
                if (parentComment != null && parentComment.getParentComment() != null) {
                    throw new RuntimeException("대댓글에 댓글을 달수 없습니다.");
                }
            }

            Comment comment = Comment.builder()
                    .parentComment(parentComment)
                    .content(content)
                    .users(user)
                    .commentText(commentRequestDto.getCommentText())
                    .status(commentRequestDto.getStatus())
                    .commentDate(commentRequestDto.getCommentDate())
                    .build();

            Comment savedComment = commentRepository.save(comment);
            return convertToDto(savedComment);
        } else {
            throw new RuntimeException("Content or User not found");
        }
    }
    @Transactional
    public void deleteComment(Long groupId,Long commentId) {
        Long userId = SecurityUtil.getCurrentUserId();
        GroupStatus status = groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId)
                .orElseThrow(() -> new NoSuchElementException("가족에서 확인되지 않는 유저입니다.")).getStatus();
        if(status == GroupStatus.BANNED || status == GroupStatus.NON_MEMBER){
            throw new IllegalStateException("가족에서 확인되지 않는 유저입니다");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글을 찾을 수 없습니다."));
        // 댓글 작성자 확인
        if (!comment.getUsers().getUserId().equals(userId)) {
            throw new IllegalStateException("댓글 작성자만 삭제할 수 있습니다.");
        }
        // 댓글의 상태를 "d"로 변경
        comment.setStatus("d");
        commentRepository.save(comment);
    }

    @Transactional
    public CommentDto  updateComment(Long groupId, Long commentId, String newText){
        Long userId = SecurityUtil.getCurrentUserId();
        Comment comment = commentRepository.findByCommentId(commentId)
                .orElseThrow(() -> new NoSuchElementException("해당 댓글을 찾을수 없습니다."));
        GroupStatus status = groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId)
                .orElseThrow(() -> new NoSuchElementException("가족에서 확인되지 않는 유저입니다.")).getStatus();
        if(status == GroupStatus.BANNED || status == GroupStatus.NON_MEMBER){
            throw new IllegalStateException("가족에서 확인되지 않는 유저입니다");
        }
        if(!comment.getUsers().getUserId().equals(userId)){
            throw new IllegalStateException("댓글 작성자만 수정할 수 있습니다.");
        }
        comment.setCommentText(newText);
        Comment save = commentRepository.save(comment);
        return convertToDto(save);
    }

    // 댓글 조회 메서드 추가
    public List<CommentDto> getCommentsByContentId(Long groupId, Long contentId) {
        Long userId = SecurityUtil.getCurrentUserId();
        GroupStatus status = groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId)
                .orElseThrow(() -> new NoSuchElementException("가족에서 확인되지 않는 유저입니다.")).getStatus();
        if (status == GroupStatus.BANNED || status == GroupStatus.NON_MEMBER) {
            throw new IllegalStateException("가족에서 확인되지 않는 유저입니다");
        }

        List<Comment> comments = commentRepository.findByContent_ContentIdAndStatus(contentId, "a");
        return comments.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    //댓글에 리엑션추가
    @Transactional
    public CommentReactionDto addOrUpdateReaction(Long commentId, Long userId, ReactionType newReaction) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));


        CommentReactionId id = new CommentReactionId(commentId,userId);

        Optional<CommentReaction> existingReaction = commentReactionRepository.findById(id);

        if (existingReaction.isPresent()) {
            CommentReaction commentReaction = existingReaction.get();
            if(commentReaction.getReaction() == newReaction){
                removeReaction(commentId, userId);
                return null;
            }
            decreaseReactionCount(comment,commentReaction.getReaction());
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을수 없습니다."));
        CommentReaction commentReaction = new CommentReaction(id, comment, user, newReaction);
        commentReactionRepository.save(commentReaction);
        increaseReactionCount(comment,newReaction);
        commentRepository.save(comment);
        CommentReactionDto dto = new CommentReactionDto();
        dto.setCommentId(commentReaction.getComment().getCommentId());
        dto.setUserId(commentReaction.getUser().getUserId());
        dto.setReaction(commentReaction.getReaction());

        return dto;
    }
    //리엑션 조회
    public List<CommentReactionDto> getReactionsByCommentId(Long commentId) {
        List<CommentReaction> reactions = commentReactionRepository.findByComment_CommentId(commentId);
        return reactions.stream().map(this::reactionConvertToDto).collect(Collectors.toList());
    }

    private CommentReactionDto reactionConvertToDto(CommentReaction reaction) {
        CommentReactionDto dto = new CommentReactionDto();
        dto.setCommentId(reaction.getComment().getCommentId());
        dto.setUserId(reaction.getUser().getUserId());
        dto.setReaction(reaction.getReaction());
        return dto;
    }

    private CommentDto convertToDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentId(comment.getCommentId());
        commentDto.setParentCommentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null);
        commentDto.setContentId(comment.getContent().getContentId());
        commentDto.setUserId(comment.getUsers().getUserId());
        commentDto.setCommentText(comment.getCommentText());
        commentDto.setStatus(comment.getStatus());
        commentDto.setCommentDate(comment.getCommentDate());
        return commentDto;
    }
    private void removeReaction(Long contentId, Long userId) {
        CommentReactionId commentReactionId = new CommentReactionId(contentId, userId);
        CommentReaction commentReaction = commentReactionRepository.findById(commentReactionId)
                .orElseThrow(() -> new NoSuchElementException("리액션을 찾을 수 없습니다."));
        Comment comment = commentReaction.getComment();
        decreaseReactionCount(comment, commentReaction.getReaction());



        commentReactionRepository.delete(commentReaction);

        // Save the content
        commentRepository.save(comment);
    }

    private void decreaseReactionCount(Comment comment, ReactionType reaction) {
        switch (reaction) {
            case LIKE -> comment.setLikeCount(comment.getLikeCount() - 1);
            case LOVE -> comment.setLoveCount(comment.getLoveCount() - 1);
            case WOW -> comment.setWowCount(comment.getWowCount() - 1);
            case SAD -> comment.setSadCount(comment.getSadCount() - 1);
            case ANGRY -> comment.setAngryCount(comment.getAngryCount() - 1);
        }
    }
    private void increaseReactionCount(Comment comment, ReactionType reaction) {
        switch (reaction) {
            case LIKE -> comment.setLikeCount(comment.getLikeCount() + 1);
            case LOVE -> comment.setLoveCount(comment.getLoveCount() + 1);
            case WOW -> comment.setWowCount(comment.getWowCount() + 1);
            case SAD -> comment.setSadCount(comment.getSadCount() + 1);
            case ANGRY -> comment.setAngryCount(comment.getAngryCount() + 1);
        }
    }

}
