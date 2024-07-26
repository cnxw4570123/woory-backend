package com.woory.backend.service;

import com.woory.backend.dto.CommentDto;
import com.woory.backend.dto.CommentMapper;
import com.woory.backend.dto.CommentReplyDto;
import com.woory.backend.dto.CommentRequestDto;
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
	public Comment addComment(CommentRequestDto commentRequestDto) {
		Long groupId = contentRepository.findByContentId(commentRequestDto.getContentId())
				.orElseThrow(()-> new CustomException(ErrorCode.GROUP_NOT_FOUND))
				.getTopic().getGroup().getGroupId();
		GroupUser groupUser = groupUserRepository.findByUser_UserIdAndGroup_GroupId(
				commentRequestDto.getUserId(), groupId)
    Content content = contentRepository.findByContentId(commentRequestDto.getContentId())
			.orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));

		if (byUserUserIdAndGroupGroupId.isEmpty()) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND_IN_GROUP);
		}
		Optional<Content> contentOptional = contentRepository.findByContentId(commentRequestDto.getContentId());
		if (contentOptional.isEmpty()) {
			throw new CustomException(ErrorCode.CONTENT_NOT_FOUND);
		}
		Optional<User> userOptional = userRepository.findByUserIdWithGroupUsers(commentRequestDto.getUserId());
		if (userOptional.isEmpty()) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}
		GroupStatus status = byUserUserIdAndGroupGroupId
			.orElseThrow(() -> new NoSuchElementException("가족에서 확인되지 않는 유저입니다.")).getStatus();


		User user = userRepository.findByUserIdWithGroupUsers(commentRequestDto.getUserId())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Comment parentComment = null;
		if (commentRequestDto.getParentCommentId() != null) {
			parentComment = commentRepository.findByCommentId(commentRequestDto.getParentCommentId())
				.orElseThrow(() -> new CustomException(ErrorCode.PARENT_COMMENT_NOT_FOUND));

			if (parentComment.getParentComment() != null) {
				throw new CustomException(ErrorCode.REPLY_TO_REPLY_NOT_ALLOWED);
			}
		}

		return commentRepository.save(Comment.toComment(commentRequestDto, parentComment, content, user));


	}

	public Comment addReply(CommentRequestDto commentDto) {
		Content content = contentRepository.findByContentId(commentDto.getContentId())
			.orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));
		User user = userRepository.findByUserIdWithGroupUsers(commentDto.getUserId())
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Comment parentComment = null;

		parentComment = commentRepository.findByCommentId(commentDto.getParentCommentId())
			.orElseThrow(() -> new CustomException(ErrorCode.PARENT_COMMENT_NOT_FOUND));

		if (parentComment.getParentComment() != null) {
			throw new CustomException(ErrorCode.REPLY_TO_REPLY_NOT_ALLOWED);
		}

		return commentRepository.save(Comment.toComment(commentDto, parentComment, content, user));
	}

	@Transactional
	public void deleteCommentAndReplies(Long commentId) {
		Comment comment = commentRepository.findByCommentId(commentId)
			.orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
		// deleteRecursive(comment);
		commentRepository.delete(comment);
	}

	@Transactional
	public CommentDto updateComment(Long commentId, String newText) {
		Long userId = SecurityUtil.getCurrentUserId();
		Long groupId = commentRepository.findByCommentId(commentId)
				.orElseThrow(()-> new CustomException(ErrorCode.GROUP_NOT_FOUND))
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
		return CommentDto.fromComment(savedComment);
	}

	// 댓글 조회 메서드 추가
	public List<CommentReplyDto> getCommentsByContentId(Long contentId) {
		Long userId = SecurityUtil.getCurrentUserId();
		Long groupId = contentRepository.findByContentId(contentId)
				.orElseThrow(()-> new CustomException(ErrorCode.GROUP_NOT_FOUND))
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
