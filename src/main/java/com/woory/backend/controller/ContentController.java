package com.woory.backend.controller;

import com.woory.backend.dto.ContentReactionDto;
import com.woory.backend.entity.Comment;
import com.woory.backend.entity.Content;
import com.woory.backend.entity.ReactionType;
import com.woory.backend.service.ContentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/contents")
@Tag(name = "글작성 관련", description = "글작성 관련 API")
public class ContentController {

	private final ContentService contentService;

	@Autowired
	public ContentController(ContentService contentService) {
		this.contentService = contentService;
	}

	@Operation(summary = "content 생성")
	@PostMapping("/create")
	public ResponseEntity<String> createContent(
		@RequestParam("groupId") Long groupId,
		@RequestParam("topicId") Long topicId,
		@RequestParam("contentText") String contentText,
		@RequestPart(value = "groupPhoto", required = false) MultipartFile groupPhoto) {

		String photoPath;
		if (groupPhoto != null) {
			try {
				photoPath = savePhoto(groupPhoto); // 사진 경로 저장
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사진 저장 중 오류 발생");
			}
		} else {
			String defaultFile = new File("src/main/resources/images/").getAbsolutePath();
			photoPath = defaultFile + "default.png";
		}

		Content content = contentService.createContent(groupId, topicId, contentText, photoPath);
		return ResponseEntity.ok("컨텐츠가 생성되었습니다: " + topicId);
	}

	@Operation(summary = "content 삭제")
	@DeleteMapping("/{groupId}/{contentId}")
	public ResponseEntity<Void> deleteContent(
		@PathVariable("groupId") Long groupId,
		@PathVariable("contentId") Long contentId) {
		contentService.deleteContent(groupId, contentId);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "content 수정")
	@PutMapping("/{groupId}/{contentId}")
	public ResponseEntity<String> updateContent(
		@PathVariable("groupId") Long groupId,
		@PathVariable("contentId") Long contentId,
		@RequestParam String contentText,
		@RequestPart(value = "groupPhoto", required = false) MultipartFile contentImg) {
		String photoPath = "";
		if (contentImg != null) {
			try {
				photoPath = savePhoto(contentImg); // 사진 경로 저장
			} catch (IOException e) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사진 저장 중 오류 발생");
			}
		} else {
			String defaultFile = new File("src/main/resources/images/").getAbsolutePath();
			photoPath = defaultFile + "default.png";
		}
		Content updatedContent = contentService.updateContent(groupId, contentId, contentText, photoPath);
		return ResponseEntity.ok("컨텐츠가 수정되었습니다: " + contentId);
	}

	@Operation(summary = "content 조회")
	@GetMapping("/get")
	public ResponseEntity<List<Content>> getContentsByRegDate(@RequestBody Map<String, String> param) {
		List<Content> contents = contentService.getContentsByRegDateLike(param.get("date"));
		return ResponseEntity.ok(contents);
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
				return ResponseEntity.ok("Reaction removed successfully");
			}
			return ResponseEntity.ok(updatedReaction);
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
