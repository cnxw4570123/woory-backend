package com.woory.backend.controller;

import com.woory.backend.dto.CommentReplyDto;
import com.woory.backend.dto.CommentRequestDto;
import com.woory.backend.dto.ReplyDto;
import com.woory.backend.dto.UpdateCommentRequest;
import com.woory.backend.service.CommentService;
import com.woory.backend.utils.StatusUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/comments")
@Tag(name = "댓글 관련", description = "댓글 관련 API")
public class CommentController {
	private final CommentService commentService;

	@Autowired
	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}

	@Operation(summary = "comment 쓰기")
	@PostMapping("/add")
	public Map<String, Object> addComment(@RequestBody CommentRequestDto commentRequestDto) {
		CommentReplyDto commentReplyDto = commentService.addComment(commentRequestDto);
		Map<String, Object> response = StatusUtil.getStatusMessage("댓글이 추가되었습니다");
		response.put("data", commentReplyDto);
		return response;
	}

	@Operation(summary = "comment 댓글 쓰기")
	@PostMapping("/add/reply")
	public Map<String, Object> addreply(@RequestBody CommentRequestDto commentDto) {
		ReplyDto commentReplyDto = commentService.addReply(commentDto);
		Map<String, Object> response = StatusUtil.getStatusMessage("댓글이 추가되었습니다");
		response.put("data", commentReplyDto);
		return response;
	}

	@Operation(summary = "comment 삭제")
	@DeleteMapping("/{commentId}")
	public Map<String, Object> deleteComment(@PathVariable("commentId") Long commentId) {
		commentService.deleteCommentAndReplies(commentId);
		return StatusUtil.getStatusMessage("댓글 삭제");
	}

	@Operation(summary = "comment 수정")
	@PutMapping("/{commentId}")
	public Map<String, Object> updateComment(
		@PathVariable("commentId") Long commentId,
		@RequestBody UpdateCommentRequest updateRequest) {

		Map<String, String> updatedComment = commentService.updateComment(commentId, updateRequest.getNewText());
		Map<String, Object> response = StatusUtil.getStatusMessage("댓글이 수정되었습니다");
		response.put("data", updatedComment);
		return response;
	}

	@Operation(summary = "comment 조회")
	@GetMapping("/{contentId}")
	public Map<String, Object> getCommentsByContentId(@PathVariable("contentId") Long contentId) {
		// List<CommentReplyDto> comments = commentService.getCommentsByContentId(contentId);
		// Map<String, Object> response = StatusUtil.getStatusMessage("댓글이 조회되었습니다");
		// response.put("data", comments);
		return commentService.getCommentsByContentId(contentId);
	}

}
