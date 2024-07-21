package com.woory.backend.controller;

import com.woory.backend.dto.UserResponseDto;
import com.woory.backend.service.UserService;
import com.woory.backend.utils.CookieUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("v1/users")
@AllArgsConstructor
@Tag(name = "사용자 관련", description = "사용자 관련 API")
public class UserController {
	private UserService userService;

	@GetMapping("/my")
	@Operation(summary = "회원 조회")
	public ResponseEntity<UserResponseDto> my() {
		return ResponseEntity.ok(userService.getUserInfo());
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

	// 가족 프로필 수정
	@PostMapping("/update/{groupId}")
	public ResponseEntity<Void> updateGroupUserProfile(
		@PathVariable("groupId") Long groupId,
		@RequestParam("groupUsername") String groupUsername,
		@RequestPart(value = "userPhoto", required = false) MultipartFile image) {
		return ResponseEntity.ok().build();

	}
}
