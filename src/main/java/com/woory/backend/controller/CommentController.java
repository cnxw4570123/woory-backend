package com.woory.backend.controller;

import com.woory.backend.dto.CommentDto;
import com.woory.backend.dto.CommentReplyDto;
import com.woory.backend.dto.CommentRequestDto;
import com.woory.backend.dto.UpdateCommentRequest;
import com.woory.backend.service.CommentService;
import com.woory.backend.utils.StatusUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/v1/comments")
@Tag(name = "댓글 관련", description = "댓글 관련 API")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {this.commentService = commentService;}


    @Operation(summary = "comment 쓰기")
    @PostMapping("/add")
//    @PostMapping("/add/{groupId}")변경경
    public ResponseEntity<Map<String, Object>> addComment(@RequestBody CommentRequestDto commentRequestDto) {
        commentService.addComment(commentRequestDto);
        Map<String, Object> response = StatusUtil.getStatusMessage("댓글이 추가되었습니다");
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "comment 댓글 쓰기")
    @PostMapping("/add/reply")
    public ResponseEntity<Map<String, Object>> addreply(@RequestBody CommentRequestDto commentDto) {
        commentService.addReply(commentDto);
        Map<String, Object> response = StatusUtil.getStatusMessage("댓글이 추가되었습니다");
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "comment 삭제")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long commentId) {
        commentService.deleteCommentAndReplies(commentId);
        return StatusUtil.getResponseMessage("댓글 삭제");
    }
    @Operation(summary = "comment 수정")
    @PutMapping("/{commentId}")
//    @PutMapping("/{groupId}/{commentId}")
    public ResponseEntity<Map<String, Object>> updateComment(
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest updateRequest) {

        CommentDto updatedComment = commentService.updateComment(commentId, updateRequest.getNewText());
        Map<String, Object> response = StatusUtil.getStatusMessage("댓글이 수정되었습니다");
        response.put("data", updatedComment);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "comment 조회")
    @GetMapping("/{contentId}")
//    @GetMapping("/{groupId}/{contentId}")
    public ResponseEntity<Map<String, Object>> getCommentsByContentId(@PathVariable Long contentId) {
        List<CommentReplyDto> comments = commentService.getCommentsByContentId(contentId);
        Map<String, Object> response = StatusUtil.getStatusMessage("댓글이 조회되었습니다");
        response.put("data", comments);
        return ResponseEntity.ok(response);
    }



}
