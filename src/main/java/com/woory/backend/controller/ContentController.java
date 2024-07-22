package com.woory.backend.controller;

import com.woory.backend.dto.ContentDto;
import com.woory.backend.dto.ContentReactionDto;
import com.woory.backend.entity.Content;
import com.woory.backend.entity.ReactionType;
import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;
import com.woory.backend.service.ContentService;

import com.woory.backend.utils.StatusUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/v1/contents")
@Tag(name = "글작성 관련", description = "글작성 관련 API")
public class ContentController {

	private final ContentService contentService;

	@Autowired
	public ContentController(ContentService contentService) {
		this.contentService = contentService;
	}

	@GetMapping("/detail/{contentId}")
	public ContentDto getContentById(@PathVariable Long contentId) {
		return contentService.getContentById(contentId);
	}

	@Operation(summary = "content 생성")
	@PostMapping("/create")
	public ResponseEntity<Map<String, Object>> createContent(
			@RequestParam("groupId") Long groupId,
			@RequestParam("topicId") Long topicId,
			@RequestParam("contentText") String contentText,
			@RequestPart(value = "contentPhoto", required = false) MultipartFile contentPhoto) {

		String photoPath = "";
		if (contentPhoto != null) {
			try {
				photoPath = savePhoto(contentPhoto);
			} catch (IOException e) {
				StatusUtil.getPhotoSaveError();
			}
		} else {
			String defaultFile = new File("src/main/resources/images/").getAbsolutePath();
			photoPath = defaultFile + "default.png";
		}

		Content content = contentService.createContent(groupId, topicId, contentText, photoPath);
		Map<String, Object> response = StatusUtil.getStatusMessage("컨텐츠가 생성되었습니다: ");
//		response.put("data", content);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "content 수정")
	@PutMapping("/{groupId}/{contentId}")
	public ResponseEntity<Map<String, Object>> updateContent(
			@PathVariable("groupId") Long groupId,
			@PathVariable("contentId") Long contentId,
			@RequestParam String contentText,
			@RequestPart(value = "contentPhoto", required = false) MultipartFile contentImg) {

		String photoPath = "";
		if (contentImg != null) {
			try {
				photoPath = savePhoto(contentImg);
			} catch (IOException e) {
				StatusUtil.getPhotoSaveError();
			}
		} else {
			String defaultFile = new File("src/main/resources/images/").getAbsolutePath();
			photoPath = defaultFile + "default.png";
		}

		Content updatedContent = contentService.updateContent(groupId, contentId, contentText, photoPath);
		Map<String, Object> response = StatusUtil.getStatusMessage("컨텐츠가 수정되었습니다.");
//		response.put("data", updatedContent);
		return ResponseEntity.ok(response);
	}
	@Operation(summary = "content 일 조회")
	@GetMapping("/get/{contentId}")
	public ResponseEntity<Map<String, Object>> getContents(@PathVariable Long contentId) {

		ContentDto content = contentService.getContent(contentId);
		Map<String, Object> response = StatusUtil.getStatusMessage("컨텐츠가 조회되었습니다");
		response.put("data", content);
		return ResponseEntity.ok(response);
	}



	@Operation(summary = "content 일 조회")
	@GetMapping("/get/day")
	public ResponseEntity<Map<String, Object>> getContentsByRegDate(@RequestParam Long groupId,@RequestParam String param) {
		if (param == null || !param.matches("\\d{4}-\\d{2}-\\d{2}")) {
			throw new CustomException(ErrorCode.INVALID_DATE_FORMAT);
		}
		List<ContentDto> contents = contentService.getContentsByRegDateLike(groupId,param);
		Map<String, Object> response = StatusUtil.getStatusMessage("컨텐츠가 조회되었습니다");
		response.put("data", contents);
		return ResponseEntity.ok(response);
	}
	@Operation(summary = "content 월 조회")
	@GetMapping("/get/month")
	public ResponseEntity<Map<String, Object>> getContentsByRegDateMonth(@RequestParam Long groupId,@RequestParam String param) {
		if (param == null || !param.matches("\\d{4}-\\d{2}")) {
			throw new CustomException(ErrorCode.INVALID_DATE_FORMAT);
		}
		List<ContentDto> contents = contentService.getContentsByRegDateLike(groupId,param);
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

	// 사진 저장 메서드
	private String savePhoto(MultipartFile photo) throws IOException {

		// 확장자 체크
		String originalFilename = photo.getOriginalFilename();
		if (originalFilename == null) {
			throw new IOException("파일 이름이 유효하지 않습니다.");
		}

		String fileExtension = getFileExtension(originalFilename);
		if (!fileExtension.equalsIgnoreCase("png") && !fileExtension.equalsIgnoreCase("jpg")) {
			throw new IOException("파일 확장자는 png 또는 jpg만 가능합니다.");
		}

		String folderPath = new File("src/main/resources/images/").getAbsolutePath() + "/";
		String fileName = UUID.randomUUID() + "_" + originalFilename;
		File file = new File(folderPath + fileName);
		photo.transferTo(file); // 파일 저장

		return file.getAbsolutePath(); // 사진 경로 반환
	}

	// 파일 확장자 추출 메서드
	private String getFileExtension(String filename) {
		int lastIndexOfDot = filename.lastIndexOf('.');
		if (lastIndexOfDot == -1) {
			return ""; // 확장자가 없는 경우 빈 문자열 반환
		}
		return filename.substring(lastIndexOfDot + 1);
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

	/**
	 * 해당 메서드도 필요가 X
	 * @param contentId
	 * @return
	 */
	@GetMapping("/reaction")
	public ResponseEntity<?> getReactions(@RequestParam Long contentId) {
		List<ContentReactionDto> reactionsByContentId = contentService.getReactionsByContentId(contentId);
		return ResponseEntity.ok(reactionsByContentId);
	}

}
