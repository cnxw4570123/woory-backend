package com.woory.backend.controller;

import com.woory.backend.entity.Content;
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
import java.util.UUID;

@RestController
@RequestMapping("/v1/content")
@Tag(name = "글작성 관련", description = "글작성 관련 API")
public class ContentController {

    private final ContentService contentService;

    @Autowired
    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @Operation(summary = "그룹 생성", description = "이름과 파일을 받아서 가족 생성, 파일 미전송 시 기본 파일으로 지정")
    // 그룹 생성
    @PostMapping("/create")
    public ResponseEntity<String> createContent(
            @RequestParam Long groupId,
            @RequestParam Long topicId,
            @RequestParam String contentText,
            @RequestPart(value = "groupPhoto", required = false) MultipartFile groupPhoto){

        String photoPath;
        if(groupPhoto != null){
            try {
                photoPath = savePhoto(groupPhoto); // 사진 경로 저장
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사진 저장 중 오류 발생");
            }
        } else{
            String defaultFile = new File("src/main/resources/images/").getAbsolutePath();
            photoPath = defaultFile + "default.png";
        }

        Content content = contentService.createContent(groupId,topicId, contentText, photoPath);
        return ResponseEntity.ok("컨텐츠가 생성되었습니다: " + topicId);
    }

    @DeleteMapping("/{groupId}/{contentId}")
    public ResponseEntity<Void> deleteContent(
            @PathVariable Long groupId,
            @PathVariable Long contentId) {
        contentService.deleteContent(groupId, contentId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{groupId}/{contentId}")
    public ResponseEntity<String> updateContent(
            @PathVariable Long groupId,
            @PathVariable Long contentId,
            @RequestParam String contentText,
            @RequestPart(value = "groupPhoto", required = false) MultipartFile contentImg) {
        String photoPath = "";
        if(contentImg != null){
            try {
                photoPath = savePhoto(contentImg); // 사진 경로 저장
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사진 저장 중 오류 발생");
            }
        } else{
            String defaultFile = new File("src/main/resources/images/").getAbsolutePath();
            photoPath = defaultFile + "default.png";
        }
        Content updatedContent = contentService.updateContent(groupId, contentId, contentText, photoPath);
        return ResponseEntity.ok("컨텐츠가 수정되었습니다: " + contentId);
    }

    // 사진 저장 메서드
    private String savePhoto(MultipartFile photo) throws IOException {
        // 사진 크기 제한 설정 (100MB)
        long maxSize = 100 * 1024 * 1024; // 100MB

        // 사진 크기 확인
        if (photo.getSize() > maxSize) {
            throw new IOException("사진 크기는 100MB를 초과할 수 없습니다.");
        }

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
}
