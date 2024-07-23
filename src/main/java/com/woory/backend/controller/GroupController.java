package com.woory.backend.controller;

import com.woory.backend.dto.DataDto;
import com.woory.backend.dto.GroupInfoDto;
import com.woory.backend.entity.Group;
import com.woory.backend.service.AwsService;
import com.woory.backend.service.GroupService;

import com.woory.backend.utils.StatusUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/groups")
@Tag(name = "가족 관련", description = "가족 관련 API")
public class GroupController {

	private final GroupService groupService;
	private final AwsService awsService;
	private final String defaultImage;

	@Autowired
	public GroupController(GroupService groupService, AwsService awsService,
		@Value("${service.default.groupImg}") String defaultImage) {
		this.groupService = groupService;
		this.awsService = awsService;
		this.defaultImage = defaultImage;
	}

	// 내 모든 가족 조회
	@GetMapping("/my")
	public ResponseEntity<Map<String, Object>> getGroups() {
		List<GroupInfoDto> groups = groupService.getMyGroups();
		Map<String, Object> response = StatusUtil.getStatusMessage("가족 정보 조회 성공했습니다");
		response.put("data", groups);
		return ResponseEntity.ok(response);
	}

	// 가족 아이디로 가족 정보 조회
	@GetMapping("/get/info/{groupId}")
	public ResponseEntity<Map<String, Object>> getGroupInfo(@PathVariable("groupId") Long groupId) {
		GroupInfoDto groupInfo = groupService.getGroupInfo(groupId);
		Map<String, Object> response = StatusUtil.getStatusMessage("가족 정보 조회에 성공했습니다.");
		response.put("data", groupInfo);
		return ResponseEntity.ok(response);
	}

	// 가족 구성원 조회
	@GetMapping("/get/{groupId}")
	public ResponseEntity<Map<String, Object>> getMyGroup(@PathVariable("groupId") Long groupId) {
		DataDto groups = groupService.getMyGroupId(groupId);
		Map<String, Object> response = StatusUtil.getStatusMessage("가족 구성원 조회에 성공했습니다");
		response.put("data", groups);
		return ResponseEntity.ok(response);
	}
	// 가족 구성원 조회
	@Operation(summary = "유저 그룹에 속한 사람인지 판단", description = "로그인 시 로그인한아이디와 그룹아이디로 가족에 속한 사람인지 판단")
	@GetMapping("/check/{groupId}")
	public ResponseEntity<Map<String, Object>> CheckUserIncludeGroup(@PathVariable("groupId") Long groupId) {
		Boolean checked = groupService.CheckUserIncludeGroup(groupId);
		Map<String, Object> response = StatusUtil.getStatusMessage("가족 정보 조회 성공했습니다");
		response.put("isInFamily", checked);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "그룹 생성", description = "이름과 파일을 받아서 가족 생성, 파일 미전송 시 기본 파일으로 지정")
	// 그룹 생성
	@PostMapping("/create")
	public ResponseEntity<Map<String, Object>> createGroup(
		@RequestParam("groupName") String groupName,
		@RequestPart(value = "images", required = false) MultipartFile groupPhoto) {
		String photoPath = awsService.saveFile(groupPhoto);
		if (photoPath == null) {
			photoPath = defaultImage;
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
		@RequestPart(value = "images", required = false) MultipartFile groupPhoto) {
		String photoPath = awsService.saveFile(groupPhoto);
		Group updatedGroup = groupService.updateGroup(groupId, groupName, photoPath);
		Map<String, Object> response = StatusUtil.getStatusMessage("가족이 수정되었습니다");
		//        response.put("data", updatedGroup);
		return ResponseEntity.ok(response);
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
	public ResponseEntity<Map<String, Object>> leaveGroup(@PathVariable("groupId") Long groupId) {
		Boolean check = groupService.leaveGroup(groupId);
		Map<String, Object> response;
		if(check == null){
			response = StatusUtil.getStatusMessage("가족이 삭제되었습니다.");
		}else{
			response = StatusUtil.getStatusMessage("가족에서 나왔습니다.");
			response.put("isLastMember", check);
		}
		return ResponseEntity.ok(response);
    }

	// 그룹 사용자를 벤하기 (그룹장 전용)
	@Operation(summary = "가족 구성원 추방")
	@PostMapping("/ban/{groupId}/user/{userId}")
	public ResponseEntity<Map<String, String>> banGroupUser(@PathVariable("groupId") Long groupId,
		@PathVariable("userId") Long userId) {
		groupService.banGroup(groupId, userId);
		return StatusUtil.getResponseMessage("사용자가 추방되었습니다.");
	}

	// 초대 링크 생성
	@Deprecated
	@Operation(summary = "가족 초대 링크 생성", description = "임시로 서버 주소 + 엔드포인트 매핑 해놓았는데 추후 수정 예정입니다.")
	// GET 요청
	@GetMapping("/invite/{groupId}")
	public ResponseEntity<Map<String, Object>> generateInviteLink(@PathVariable("groupId") Long groupId) {
		String inviteLink = groupService.generateInviteLink(groupId);
		Map<String, Object> response = StatusUtil.getStatusMessage("초대 링크가 생성되었습니다");
		response.put("data", inviteLink);
		return ResponseEntity.ok(response);
	}

	// 그룹 가입
	@Operation(summary = "가족에 참여", description = "초대 링크를 통해 그룹에 참여")
	// POST 요청
	@PostMapping("/url/{groupId}")
	public ResponseEntity<Map<String, String>> joinGroup(@PathVariable("groupId") Long groupId) {
		groupService.joinGroup(groupId);
		return StatusUtil.getResponseMessage("가족에 참여했습니다.");
	}

}
