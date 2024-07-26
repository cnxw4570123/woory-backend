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

import java.util.Date;
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
		Optional<GroupUser> byUserUserIdAndGroupGroupId = groupUserRepository.findByUser_UserIdAndGroup_GroupId(
				commentRequestDto.getUserId(), groupId);

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


		if (contentOptional.isPresent() && userOptional.isPresent()) {
			Content content = contentOptional.get();
			User user = userOptional.get();

			Comment parentComment = null;
			if (commentRequestDto.getParentCommentId() != null) {
				Optional<Comment> byCommentId = commentRepository.findByCommentId(commentRequestDto.getParentCommentId());
				if (byCommentId.isEmpty()) {
					throw new CustomException(ErrorCode.PARENT_COMMENT_NOT_FOUND);
				}
				parentComment = byCommentId.get();
				if (parentComment != null && parentComment.getParentComment() != null) {
					throw new CustomException(ErrorCode.REPLY_TO_REPLY_NOT_ALLOWED);
				}
			}
			Date now = new Date();


			Comment comment = new Comment();
			comment.setCommentText(commentRequestDto.getCommentText());
			comment.setCommentDate(now);
			comment.setContent(content);
			comment.setUsers(user);
			comment.setParentComment(parentComment);

			return commentRepository.save(comment);
		} else {
			throw new RuntimeException("Content or User not found");
		}
	}

	public  Comment addReply(CommentRequestDto commentDto) {

		Optional<User> userOptional = userRepository.findByUserIdWithGroupUsers(commentDto.getUserId());
		Optional<Content> contentOptional = contentRepository.findByContentId(commentDto.getContentId());
		Content content = contentOptional
				.orElseThrow(()-> new CustomException(ErrorCode.CONTENT_NOT_FOUND));
		User user = userOptional
				.orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

		Comment parentComment = null;

		Optional<Comment> byCommentId = commentRepository.findByCommentId(commentDto.getParentCommentId());

		if (byCommentId.isEmpty()) {
			throw new CustomException(ErrorCode.PARENT_COMMENT_NOT_FOUND);
		}
		parentComment = byCommentId.get();
		if (parentComment != null && parentComment.getParentComment() != null) {
			throw new CustomException(ErrorCode.REPLY_TO_REPLY_NOT_ALLOWED);
		}
		Date now = new Date();

		Comment comment = new Comment();
		comment.setCommentText(commentDto.getCommentText());
		comment.setCommentDate(now);
		comment.setContent(content);
		comment.setUsers(user);
		comment.setParentComment(parentComment);
		return commentRepository.save(comment);
	}

	@Transactional
	public void deleteCommentAndReplies(Long commentId) {
		Comment comment = commentRepository.findByCommentId(commentId)
				.orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
		deleteRecursive(comment);
	}

	@Transactional
	public CommentDto updateComment(Long commentId, String newText) {
		Long userId = SecurityUtil.getCurrentUserId();
		Long groupId = commentRepository.findByCommentId(commentId)
				.orElseThrow(()-> new CustomException(ErrorCode.GROUP_NOT_FOUND))
				.getContent().getTopic().getGroup().getGroupId();
		Comment comment = commentRepository.findByCommentId(commentId)
				.orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
		GroupStatus status = groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId)
				.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_IN_GROUP)).getStatus();


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
		GroupStatus status = groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_IN_GROUP)).getStatus();

		List<Comment> comments = commentRepository.findByContent_ContentId(contentId);
		List<Comment> topLevelComments = comments.stream()
				.filter(comment -> comment.getParentComment() == null)
				.collect(Collectors.toList());
		return topLevelComments.stream()
				.map(comment -> {
					return CommentMapper.toDTO(comment,userId);
				})
				.collect(Collectors.toList());
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
