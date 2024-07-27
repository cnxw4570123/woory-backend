package com.woory.backend.service;

import com.woory.backend.dto.CommentDto;
import com.woory.backend.dto.CommentMapper;
import com.woory.backend.dto.CommentReplyDto;
import com.woory.backend.dto.CommentRequestDto;
import com.woory.backend.dto.ReplyDto;
import com.woory.backend.entity.*;
import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;
import com.woory.backend.repository.*;
import com.woory.backend.utils.SecurityUtil;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

	private CommentRepository commentRepository;
	private ContentRepository contentRepository;
	private UserRepository userRepository;
	private GroupUserRepository groupUserRepository;

	@Autowired
	public CommentService(CommentRepository commentRepository, ContentRepository contentRepository,
		UserRepository userRepository, GroupUserRepository groupUserRepository) {
		this.commentRepository = commentRepository;
		this.contentRepository = contentRepository;
		this.userRepository = userRepository;
		this.groupUserRepository = groupUserRepository;
	}

	@Transactional
	public ReplyDto addComment(CommentRequestDto commentRequestDto) {
		Content content = contentRepository.findByContentId(commentRequestDto.getContentId())
			.orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));
		Long groupId = content.getTopic().getGroup().getGroupId();

		Long userId = SecurityUtil.getCurrentUserId();

		groupUserRepository.findByUser_UserIdAndGroup_GroupId(
				userId, groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_IN_GROUP));

		User user = userRepository.findByUserIdWithGroupUsers(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Comment parentComment = null;
		if (commentRequestDto.getParentCommentId() != null) {
			parentComment = commentRepository.findByCommentId(commentRequestDto.getParentCommentId())
				.orElseThrow(() -> new CustomException(ErrorCode.PARENT_COMMENT_NOT_FOUND));

			if (parentComment.getParentComment() != null) {
				throw new CustomException(ErrorCode.REPLY_TO_REPLY_NOT_ALLOWED);
			}
		}

		Comment save = commentRepository.save(Comment.toComment(commentRequestDto, parentComment, content, user));
		return CommentMapper.toReplyDTO(save, userId);

	}

	public ReplyDto addReply(CommentRequestDto commentDto) {
		Content content = contentRepository.findByContentId(commentDto.getContentId())
			.orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));
		Long groupId = content.getTopic().getGroup().getGroupId();

		User user = userRepository.findByUserIdWithGroupUsers(SecurityUtil.getCurrentUserId())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		groupUserRepository.findByUser_UserIdAndGroup_GroupId(user.getUserId(), groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_IN_GROUP));

		Comment parentComment = null;

		parentComment = commentRepository.findByCommentId(commentDto.getParentCommentId())
			.orElseThrow(() -> new CustomException(ErrorCode.PARENT_COMMENT_NOT_FOUND));

		if (parentComment.getParentComment() != null) {
			throw new CustomException(ErrorCode.REPLY_TO_REPLY_NOT_ALLOWED);
		}

		Comment save = commentRepository.save(Comment.toComment(commentDto, parentComment, content, user));
		return CommentMapper.toReplyDTO(save, user.getUserId());
	}

	@Transactional
	public void deleteCommentAndReplies(Long commentId) {
		Comment comment = commentRepository.findByCommentId(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

		User writtenUser = comment.getUsers();
		if (!writtenUser.getUserId().equals(SecurityUtil.getCurrentUserId())) {
			throw new CustomException(ErrorCode.NOT_COMMENT_AUTHOR);
		}
		// deleteRecursive(comment);
		commentRepository.delete(comment);
	}

	@Transactional
	public ReplyDto updateComment(Long commentId, String newText) {
		Long userId = SecurityUtil.getCurrentUserId();
		Long groupId = commentRepository.findByCommentId(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND))
			.getContent().getTopic().getGroup().getGroupId();
		Comment comment = commentRepository.findByCommentId(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
		groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_IN_GROUP));

		if (!comment.getUsers().getUserId().equals(userId)) {
			throw new CustomException(ErrorCode.NOT_COMMENT_AUTHOR);
		}

		comment.setCommentText(newText);
		Comment savedComment = commentRepository.save(comment);
		return CommentMapper.toReplyDTO(savedComment, userId);
	}

	// 댓글 조회 메서드 추가
	public List<CommentReplyDto> getCommentsByContentId(Long contentId) {
		Long userId = SecurityUtil.getCurrentUserId();
		Long groupId = contentRepository.findByContentId(contentId)
			.orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND))
			.getTopic().getGroup().getGroupId();

		groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_IN_GROUP));

		List<Comment> comments = commentRepository.findByContent_ContentId(contentId);
		List<Comment> topLevelComments = comments.stream()
			.filter(comment -> comment.getParentComment() == null)
			.toList();
		return topLevelComments.stream()
			.map(comment -> {
				return CommentMapper.toDTO(comment, userId);
			}).toList();
	}

	private void deleteRecursive(Comment comment) {
		List<Comment> childComments = commentRepository.findByParentComment(comment);

		for (Comment child : childComments) {
			deleteRecursive(child);
		}

		commentRepository.delete(comment);
	}

	private boolean checkUserEditPermission(Long currentUserId, Long commentUserId) {
		// Implement your logic here to check if the current user has permission to edit
		boolean checkPermission = false;
		if (currentUserId.equals(commentUserId)) {
			checkPermission = true;
		}
		return checkPermission;
	}
}
