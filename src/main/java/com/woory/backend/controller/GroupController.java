package com.woory.backend.controller;

import com.woory.backend.entity.Group;
import com.woory.backend.service.GroupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



import java.io.File;
import java.io.IOException;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/groups")
@Tag(name = "가족 관련", description = "가족 관련 API")
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @Operation(summary = "그룹 생성", description = "이름과 파일을 받아서 가족 생성, 파일 미전송 시 기본 파일으로 지정")
    // 그룹 생성
    @PostMapping("/create")
    public ResponseEntity<String> createGroup(
            @RequestParam("groupName") String groupName,
            @RequestPart(value = "groupPhoto", required = false) MultipartFile groupPhoto) {

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

        // 그룹 생성
        Group group = groupService.createGroup(groupName, photoPath);
        return ResponseEntity.ok("그룹이 생성되었습니다: " + group.getGroupId());
    }


    @PutMapping("/update/{groupId}")
    @Operation(summary = "가족 수정", description = "가족 이름과 사진을 받아서 가족 수정")
    public ResponseEntity<String> updateGroup(
            @PathVariable("groupId") Long groupId,
            @RequestParam("groupName") String groupName,
            @RequestPart(value = "groupPhoto", required = false) MultipartFile groupPhoto) {

        String photoPath = null;

        if (groupPhoto != null && !groupPhoto.isEmpty()) {
            try {
                photoPath = savePhoto(groupPhoto);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사진 저장 중 오류 발생");
            }
        }

        Group updatedGroup = groupService.updateGroup(groupId, groupName, photoPath);
        return ResponseEntity.ok("그룹이 수정되었습니다: " + updatedGroup.getGroupId());
    }


    // 그룹 삭제
    @Operation(summary = "가족 삭제")
    @DeleteMapping("delete/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("groupId") Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok().build();
    }

    // 그룹 떠나기
    @Operation(summary = "가족 나가기")
    @PostMapping("/leave/{groupId}")
    public ResponseEntity<Void> leaveGroup(@PathVariable("groupId") Long groupId) {
        groupService.leaveGroup(groupId);
        return ResponseEntity.ok().build();
    }

    // 그룹 사용자를 벤하기 (그룹장 전용)
    @Operation(summary = "가족 구성원 추방")
    @PostMapping("/ban/{groupId}/user/{userId}")
    public ResponseEntity<Void> banGroupUser(@PathVariable("groupId") Long groupId, @PathVariable("userId") Long userId) {
        groupService.banGroup(groupId, userId);
        return ResponseEntity.ok().build();
    }

    // 초대 링크 생성
    @Operation(summary = "가족 초대 링크 생성", description = "임시로 서버 주소 + 엔드포인트 매핑 해놓았는데 추후 수정 예정입니다.")
    @PostMapping("/invite/{groupId}")
    public ResponseEntity<String> generateInviteLink(@PathVariable("groupId") Long groupId) {
        String inviteLink = groupService.generateInviteLink(groupId);
        return ResponseEntity.ok(inviteLink);
    }

    // 그룹 가입
    @Operation(summary = "가족에 참여", description = "초대 링크를 통해 그룹에 참여")
    @GetMapping("/url/{groupId}")
    public ResponseEntity<Void> joinGroup(@PathVariable("groupId") Long groupId) {
        groupService.joinGroup(groupId);
        return ResponseEntity.ok().build();
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
