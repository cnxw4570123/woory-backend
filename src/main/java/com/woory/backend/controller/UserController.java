package com.woory.backend.controller;

import java.util.HashMap;
import java.util.Map;

import com.woory.backend.dto.UserResponseDto;
import com.woory.backend.service.AwsService;
import com.woory.backend.service.UserService;
import com.woory.backend.utils.CookieUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
@Tag(name = "사용자 관련", description = "사용자 관련 API")
public class UserController {
	private final UserService userService;
	private final AwsService awsService;

	@GetMapping("/my/{groupId}")
	@Operation(summary = "회원 조회")
	public ResponseEntity<UserResponseDto> my(@PathVariable("groupId") long groupId) {
		return ResponseEntity.ok(userService.getUserInfo(groupId));
	}

	@GetMapping("/logout")
	@Operation(summary = "서비스 로그아웃")
	public ResponseEntity<Void> logout(HttpServletResponse response) {
		ResponseCookie cookie = CookieUtil.createAccessTokenCookie("", 0);
		response.setHeader("Set-Cookie", cookie.toString());
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/delete")
	@Operation(summary = "서비스 탈퇴")
	public ResponseEntity<Void> deleteAccount(HttpServletResponse response) {
		userService.deleteAccount();
		// 쿠키 삭제
		response.setHeader("Set-Cookie", CookieUtil.createAccessTokenCookie("", 0).toString());
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	// 프로필 수정
	@PutMapping("/update")
	public ResponseEntity<Map<String, String>> updateGroupUserProfile(
		@RequestPart("nickname") String nickname,
		@RequestPart(value = "userPhoto", required = false) MultipartFile image) {
		String photoPath = awsService.saveFile(image);
		userService.updateProfile(photoPath, nickname);
		Map<String, String> response = new HashMap<>();
		response.put("message", "수정이 완료되었습니다.");

		return ResponseEntity.ok(response);
	}
}
