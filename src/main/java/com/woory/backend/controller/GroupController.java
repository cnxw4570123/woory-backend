package com.woory.backend.controller;

import com.woory.backend.dto.DataDto;
import com.woory.backend.dto.GroupDto;
import com.woory.backend.dto.GroupInfoDto;
import com.woory.backend.entity.Group;
import com.woory.backend.service.GroupService;

import com.woory.backend.utils.PhotoUtils;
import com.woory.backend.utils.StatusUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    // 그룹 조회
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getGroups() {
        List<GroupInfoDto> groups = groupService.getMyGroups();
        Map<String, Object> response = StatusUtil.getStatusMessage("가족 정보 조회 성공했습니다");
        response.put("data", groups);
        return ResponseEntity.ok(response);
    }
 
    // 그룹 조회
    @GetMapping("/get/{groupId}")
    public ResponseEntity<Map<String, Object>> getMyGroup(@PathVariable("groupId") Long groupId) {
        DataDto groups = groupService.getMyGroupId(groupId);
        Map<String, Object> response = StatusUtil.getStatusMessage("가족 정보 조회 성공했습니다");
        response.put("data", groups);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "그룹 생성", description = "이름과 파일을 받아서 가족 생성, 파일 미전송 시 기본 파일으로 지정")
    // 그룹 생성
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createGroup(
            @RequestParam("groupName") String groupName,
            @RequestPart(value = "groupPhoto", required = false) MultipartFile groupPhoto) {

        String photoPath;
        if (groupPhoto != null) {
            try {
                photoPath = savePhoto(groupPhoto);
            } catch (IOException e) {
                return StatusUtil.getPhotoSaveError();
            }
        } else {
            String defaultFile = new File("src/main/resources/images/").getAbsolutePath();
            photoPath = defaultFile + "default.png";
        }

        Group group = groupService.createGroup(groupName, photoPath);
        Map<String, Object> response = StatusUtil.getStatusMessage("가족이 생성되었습니다.");
        response.put("groupId", group.getGroupId());
        return ResponseEntity.ok(response);
    }



    @PutMapping("/update/{groupId}")
	  @Operation(summary = "가족 수정", description = "가족 이름과 사진을 받아서 가족 수정")
	  public ResponseEntity<Map<String, Object>> updateGroup(
		  @PathVariable("groupId") Long groupId,
		  @RequestParam("groupName") String groupName,
		  @RequestPart(value = "groupPhoto", required = false) MultipartFile groupPhoto) {
		  try {
			  String photoPath = PhotoUtils.handlePhoto(groupPhoto);
			  Group updatedGroup = groupService.updateGroup(groupId, groupName, photoPath);
			  Map<String, Object> response = StatusUtil.getStatusMessage("가족이 수정되었습니다");
			  //        response.put("data", updatedGroup);
			  return ResponseEntity.ok(response);
		  } catch (IOException e) {
			  return StatusUtil.getPhotoSaveError();
		  }
	  }
 


    // 그룹 삭제
    @Operation(summary = "가족 삭제")
    @DeleteMapping("/delete/{groupId}")
    public ResponseEntity<Map<String, String>> deleteGroup(@PathVariable("groupId") Long groupId) {
        groupService.deleteGroup(groupId);
        return StatusUtil.getResponseMessage("가족이 삭제되었습니다.");
    }
  

    // 그룹 떠나기
    @Operation(summary = "가족 나가기")
    @PostMapping("/leave/{groupId}")
    public ResponseEntity<Map<String, String>> leaveGroup(@PathVariable("groupId") Long groupId) {
        groupService.leaveGroup(groupId);
        return StatusUtil.getResponseMessage("가족에서 나왔습니다.");
    }

    // 그룹 사용자를 벤하기 (그룹장 전용)
    @Operation(summary = "가족 구성원 추방")
    @PostMapping("/ban/{groupId}/user/{userId}")
    public ResponseEntity<Map<String, String>> banGroupUser(@PathVariable("groupId") Long groupId, @PathVariable("userId") Long userId) {
        groupService.banGroup(groupId, userId);
        return StatusUtil.getResponseMessage("사용자가 추방되었습니다.");
    }
  
    // 초대 링크 생성
    @Operation(summary = "가족 초대 링크 생성", description = "임시로 서버 주소 + 엔드포인트 매핑 해놓았는데 추후 수정 예정입니다.")
    @PostMapping("/invite/{groupId}")
    public ResponseEntity<Map<String, Object>> generateInviteLink(@PathVariable("groupId") Long groupId) {
        String inviteLink = groupService.generateInviteLink(groupId);
        Map<String, Object> response = StatusUtil.getStatusMessage("초대 링크가 생성되었습니다");
        response.put("data", inviteLink);
        return ResponseEntity.ok(response);
    }

    // 그룹 가입
    @Operation(summary = "가족에 참여", description = "초대 링크를 통해 그룹에 참여")
    @GetMapping("/url/{groupId}")
    public ResponseEntity<Map<String, String>> joinGroup(@PathVariable("groupId") Long groupId) {
        groupService.joinGroup(groupId);
        return StatusUtil.getResponseMessage("가족에 참여했습니다.");
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




  
  
   
  
	

	
  


  
	

	
  
  
	

}
