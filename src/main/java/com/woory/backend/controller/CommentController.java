package com.woory.backend.controller;

import com.woory.backend.dto.CommentDto;
import com.woory.backend.dto.CommentRequestDto;
import com.woory.backend.entity.Comment;
import com.woory.backend.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<CommentDto> addComment(@PathVariable Long groupId, @RequestBody CommentRequestDto commentRequestDto) {
        CommentDto commentDto = commentService.addComment(groupId, commentRequestDto);
        return ResponseEntity.ok(commentDto);
    }
    @Operation(summary = "comment 삭제")
    @DeleteMapping("/{groupId}/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long groupId, @PathVariable Long commentId) {
        commentService.deleteComment(groupId, commentId);
        return ResponseEntity.ok().build();
    }
    @Operation(summary = "comment 수정")
    @PutMapping("/{groupId}/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long groupId, @PathVariable Long commentId, @RequestBody String newText) {
        CommentDto  updatedComment = commentService.updateComment(groupId, commentId, newText);
        return ResponseEntity.ok(updatedComment);
    }
    @Operation(summary = "comment 조회")
    @GetMapping("/{groupId}/{contentId}")
    public ResponseEntity<List<CommentDto>> getCommentsByContentId(@PathVariable Long groupId,@PathVariable Long contentId) {
        List<CommentDto> comments = commentService.getCommentsByContentId(groupId,contentId);
        return ResponseEntity.ok(comments);
    }

}
