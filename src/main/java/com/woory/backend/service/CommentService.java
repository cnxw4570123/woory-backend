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
import com.woory.backend.utils.StatusUtil;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {

	private CommentRepository commentRepository;
	private ContentRepository contentRepository;
	private UserRepository userRepository;
	private GroupUserRepository groupUserRepository;
	private NotificationRepository notificationRepository;

	@Autowired
	public CommentService(CommentRepository commentRepository, ContentRepository contentRepository,
		UserRepository userRepository, GroupUserRepository groupUserRepository,
		NotificationRepository notificationRepository) {
		this.commentRepository = commentRepository;
		this.contentRepository = contentRepository;
		this.userRepository = userRepository;
		this.groupUserRepository = groupUserRepository;
		this.notificationRepository = notificationRepository;
	}

	@Transactional
	public CommentReplyDto addComment(CommentRequestDto commentRequestDto) {
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

		Notification notification = Notification.fromCreatingComment(groupId, content.getContentId(), userId,
			save.getCommentId(), content.getUsers().getUserId(), new Date());

		notificationRepository.save(notification);

		return CommentMapper.toDTO(save, userId);

	}

	public ReplyDto addReply(CommentRequestDto commentDto) {
		Content content = contentRepository.findByContentId(commentDto.getContentId())
			.orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));
		Long groupId = content.getTopic().getGroup().getGroupId();

		Long userId = SecurityUtil.getCurrentUserId();
		User user = userRepository.findByUserIdWithGroupUsers(userId)
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

		List<Notification> notifications = new ArrayList<>();

		Date now = new Date();
		// 원 글 작성자에 대한 알림
		notifications.add(Notification.fromCreatingComment(groupId, content.getContentId(), userId, save.getCommentId(),
			content.getUsers().getUserId(), now));
		// 원 댓글 작성자에 대한 알림
		notifications.add(Notification.fromCreatingReply(groupId, content.getContentId(), userId,
			save.getCommentId(), parentComment.getUsers().getUserId(), now));
		notificationRepository.saveAll(notifications);

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
	public Map<String, String> updateComment(Long commentId, String newText) {
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

		return Collections.singletonMap("comment", savedComment.getCommentText());
	}

	// 댓글 조회 메서드 추가
	public Map<String, Object> getCommentsByContentId(Long contentId) {
		Long userId = SecurityUtil.getCurrentUserId();
		User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		Long groupId = contentRepository.findByContentId(contentId)
			.orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND))
			.getTopic().getGroup().getGroupId();
		groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_IN_GROUP));
		List<Comment> comments = commentRepository.findByContent_ContentId(contentId);
		List<Comment> topLevelComments = comments.stream()
			.filter(comment -> comment.getParentComment() == null)
			.toList();
		List<CommentReplyDto> list = topLevelComments.stream()
			.map(comment -> {
				return CommentMapper.toDTO(comment, userId);
			}).toList();

		Map<String, Object> response = StatusUtil.getStatusMessage("댓글이 조회되었습니다");
		response.put("name", user.getNickname());
		response.put("data", list);
		return response;
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
