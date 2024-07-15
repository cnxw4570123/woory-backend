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

@RestController
@RequestMapping("/v1/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

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
    @DeleteMapping("delete/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("groupId") Long groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok().build();
    }

    // 그룹 떠나기
    @PostMapping("/leave/{groupId}")
    public ResponseEntity<Void> leaveGroup(@PathVariable("groupId") Long groupId) {
        groupService.leaveGroup(groupId);
        return ResponseEntity.ok().build();
    }

    // 그룹 사용자를 벤하기 (그룹장 전용)
    @PostMapping("/ban/{groupId}/user/{userId}")
    public ResponseEntity<Void> banGroupUser(@PathVariable("groupId") Long groupId, @PathVariable("userId") Long userId) {
        groupService.banGroup(groupId, userId);
        return ResponseEntity.ok().build();
    }

    // 초대 링크 생성
    @PostMapping("/invite/{groupId}")
    public ResponseEntity<String> generateInviteLink(@PathVariable("groupId") Long groupId) {
        String inviteLink = groupService.generateInviteLink(groupId);
        return ResponseEntity.ok(inviteLink);
    }

    // 그룹 가입
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
            throw new IOException("사진 크기는 100MB를 초과할 수 없습니다");
        }
        String folderPath = new File("src/main/resources/images/").getAbsolutePath();
        String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();
        File file = new File(folderPath + fileName);
        photo.transferTo(file); // 파일 저장

        return file.getAbsolutePath(); // 사진 경로 반환
    }

}
