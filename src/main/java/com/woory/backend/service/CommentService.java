package com.woory.backend.service;

import com.woory.backend.dto.CommentDto;
import com.woory.backend.dto.CommentRequestDto;
import com.woory.backend.entity.*;
import com.woory.backend.repository2.CommentRepository;
import com.woory.backend.repository2.ContentRepository;
import com.woory.backend.repository2.GroupUserRepository;
import com.woory.backend.repository2.UserRepository;
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

    @Autowired
    public CommentService(CommentRepository commentRepository, ContentRepository contentRepository, UserRepository userRepository, GroupUserRepository groupUserRepository) {
        this.commentRepository = commentRepository;
        this.contentRepository = contentRepository;
        this.userRepository = userRepository;
        this.groupUserRepository = groupUserRepository;
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
}
