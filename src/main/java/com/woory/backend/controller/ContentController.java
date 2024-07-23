package com.woory.backend.controller;

import com.woory.backend.dto.ContentDto;
import com.woory.backend.dto.ContentReactionDto;

import com.woory.backend.dto.ContentRequestDto;
import com.woory.backend.dto.TopicDto;
import com.woory.backend.entity.Content;
import com.woory.backend.entity.ReactionType;
import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;
import com.woory.backend.service.AwsService;
import com.woory.backend.service.ContentService;

import com.woory.backend.utils.PhotoUtils;
import com.woory.backend.utils.StatusUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/contents")
@Tag(name = "글작성 관련", description = "글작성 관련 API")
public class ContentController {
	private static final SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");

	private final ContentService contentService;
	private final AwsService awsService;

	@Autowired
	public ContentController(ContentService contentService, AwsService awsService) {
		this.contentService = contentService;
		this.awsService = awsService;
	}

	// 컨텐츠 조회 - 글만 조회
	@GetMapping("/detail/{contentId}")
	public ContentDto getContentById(@PathVariable Long contentId) {
		return contentService.getContentById(contentId);
	}

	@Operation(summary = "content 생성")
	@PostMapping("/create")
	public ResponseEntity<Map<String, Object>> createContent(@RequestBody ContentRequestDto requestDto) {

		String photoPath = awsService.saveFile(requestDto.getImages());
		Content content = contentService.createContent(requestDto.getGroupId(), requestDto.getTopicId(),
			requestDto.getContentText(), photoPath);
		Map<String, Object> response = StatusUtil.getStatusMessage("컨텐츠가 생성되었습니다: ");
		//		response.put("data", content);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "content 수정")
	@PutMapping("/{groupId}/{contentId}")
	public ResponseEntity<Map<String, Object>> updateContent(
		@PathVariable("groupId") Long groupId,
		@PathVariable("contentId") Long contentId,
		@RequestBody ContentRequestDto requestDto) {
		String photoPath = awsService.saveFile(requestDto.getImages());
		Content updatedContent = contentService.updateContent(groupId, contentId, requestDto.getContentText(),
			photoPath);
		Map<String, Object> response = StatusUtil.getStatusMessage("컨텐츠가 수정되었습니다.");
		response.put("data", updatedContent);
		return ResponseEntity.ok(response);
	}

	// @Operation(summary = "단독 content 조회")
	// @GetMapping("/get/{contentId}")
	// public ResponseEntity<Map<String, Object>> getContents(@PathVariable Long contentId) {
	// 	ContentDto content = contentService.getContent(contentId);
	// 	Map<String, Object> response = StatusUtil.getStatusMessage("컨텐츠가 조회되었습니다");
	// 	response.put("data", content);
	// 	return ResponseEntity.ok(response);
	// }

	@Operation(summary = "그룹 내 일간 컨텐츠 모두 조회")
	@GetMapping("/{groupId}/get")
	public ResponseEntity<Map<String, Object>> getContentsByRegDate(
		@PathVariable("groupId") Long groupId,
		@RequestParam("day") String day) {
		LocalDate parse;
		if (day == null || !day.matches("\\d{4}-\\d{2}-\\d{2}")) {
			throw new CustomException(ErrorCode.INVALID_DATE_FORMAT);
		}
		try {
			parse = LocalDate.parse(day);
		} catch (DateTimeParseException e) {
			throw new CustomException(ErrorCode.INVALID_DATE_FORMAT);
		}

		TopicDto topic = contentService.getTopic(parse, groupId);
		Map<String, Object> response = StatusUtil.getStatusMessage("컨텐츠가 조회되었습니다");
		response.put("data", topic);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "content 월 조회")
	@GetMapping("/get/month")
	public ResponseEntity<Map<String, Object>> getContentsByRegDateMonth(@RequestParam Long groupId,
		@RequestParam String param) {
		if (param == null || !param.matches("\\d{4}-\\d{2}")) {
			throw new CustomException(ErrorCode.INVALID_DATE_FORMAT);
		}
		List<ContentDto> contents = contentService.getContentsByRegDateMonthLike(groupId,param);
		Map<String, Object> response = StatusUtil.getStatusMessage("컨텐츠가 조회되었습니다");
		response.put("data", contents);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/content/reactions/count")
	public ResponseEntity<Map<String, Object>> getReactionCounts(@RequestParam Long contentId) {
		Map<ReactionType, Long> reactionCounts = contentService.getReactionCounts(contentId);

		Map<String, Object> response = Map.of(
			"message", "리액션 개수가 조회되었습니다.",
			"data", reactionCounts
		);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "컨텐츠 삭제")
	@DeleteMapping("/delete/{groupId}/{contentId}")
	public ResponseEntity<Map<String, Object>> deleteContent(
		@PathVariable Long groupId,
		@PathVariable Long contentId) {
		contentService.deleteContent(groupId, contentId);
		Map<String, Object> response = StatusUtil.getStatusMessage("컨텐츠가 삭제되었습니다");
		return ResponseEntity.ok(response);
	}

	@PostMapping("/reaction")
	public ResponseEntity<?> addOrUpdateReaction(@RequestParam Long contentId,
		@RequestParam Long userId,
		@RequestParam String reaction) {
		try {
			ReactionType reactionType = ReactionType.valueOf(reaction.toUpperCase());
			ContentReactionDto updatedReaction = contentService.addOrUpdateReaction(contentId, userId, reactionType);
			if (updatedReaction == null) {
				return ResponseEntity.ok("표현이 삭제되었습니다");
			}
			return ResponseEntity.ok("표현이 추가되었습니다.");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/reaction")
	public ResponseEntity<?> getReactions(@RequestParam Long contentId) {
		List<ContentReactionDto> reactionsByContentId = contentService.getReactionsByContentId(contentId);
		return ResponseEntity.ok(reactionsByContentId);
	}

}
