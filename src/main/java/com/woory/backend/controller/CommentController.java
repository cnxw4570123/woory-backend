package com.woory.backend.controller;

import com.woory.backend.dto.CommentDto;
import com.woory.backend.dto.CommentRequestDto;
import com.woory.backend.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/comments")
@Tag(name = "댓글 관련", description = "댓글 관련 API")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {this.commentService = commentService;}


    @Operation(summary = "comment 쓰기")
    @PostMapping("/add/{groupId}")
    public ResponseEntity<Map<String, Object>> addComment(@PathVariable Long groupId, @RequestBody CommentRequestDto commentRequestDto) {
        CommentDto commentDto = commentService.addComment(groupId, commentRequestDto);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Comment added successfully");
        response.put("data", commentDto);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "comment 삭제")
    @DeleteMapping("/{groupId}/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long groupId, @PathVariable Long commentId) {
        commentService.deleteComment(groupId, commentId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "댓글 삭제");
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "comment 수정")
    @PutMapping("/{groupId}/{commentId}")
    public ResponseEntity<Map<String, Object>> updateComment(@PathVariable Long groupId, @PathVariable Long commentId, @RequestBody String newText) {
        CommentDto updatedComment = commentService.updateComment(groupId, commentId, newText);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Comment updated successfully");
        response.put("data", updatedComment);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "comment 조회")
    @GetMapping("/{groupId}/{contentId}")
    public ResponseEntity<Map<String, Object>> getCommentsByContentId(@PathVariable Long groupId, @PathVariable Long contentId) {
        List<CommentDto> comments = commentService.getCommentsByContentId(groupId, contentId);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Comments retrieved successfully");
        response.put("data", comments);
        return ResponseEntity.ok(response);
    }



}
