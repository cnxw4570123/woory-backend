package com.woory.backend.controller;

import java.util.HashMap;
import java.util.Map;

import com.woory.backend.dto.UserRequestDto;
import com.woory.backend.service.AwsService;
import com.woory.backend.service.UserService;
import com.woory.backend.utils.CookieUtil;
import com.woory.backend.utils.StatusUtil;

import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "사용자 관련", description = "사용자 관련 API")
public class UserController {
	private final UserService userService;
	private final AwsService awsService;

	@GetMapping("/my")
	public Map<String, Object> myInfo() {
		Map<String, Object> statusMessage = StatusUtil.getStatusMessage("유저 정보 조회에 성공했습니다.");
		statusMessage.put("data", userService.getMyInfo());
		return statusMessage;
	}

	@GetMapping("/my/{groupId}")
	@Operation(summary = "회원 조회")
	public Map<String, Object> my(@PathVariable("groupId") long groupId) {
		Map<String, Object> response = StatusUtil.getStatusMessage("유저 정보 조회에 성공했습니다.");
		response.put("data", userService.getUserInfo(groupId));
		return response;
	}

	@GetMapping("/logout")
	@Operation(summary = "서비스 로그아웃")
	public void logout(HttpServletResponse response) {
		ResponseCookie cookie = CookieUtil.createAccessTokenCookie("", 0);
		response.setHeader("Set-Cookie", cookie.toString());
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@DeleteMapping("/delete")
	@Operation(summary = "서비스 탈퇴")
	public void deleteAccount(HttpServletResponse response) {
		userService.deleteAccount();
		// 쿠키 삭제
		response.setHeader("Set-Cookie", CookieUtil.createAccessTokenCookie("", 0).toString());
		response.setStatus(HttpServletResponse.SC_OK);
	}

	// 프로필 수정
	@PutMapping("/update")
	public Map<String, String> updateGroupUserProfile(@RequestBody UserRequestDto requestDto) {
		String photoPath = awsService.saveImage(requestDto.getImages());
		userService.updateProfile(photoPath, requestDto.getNickname());
		Map<String, String> response = new HashMap<>();
		response.put("message", "수정이 완료되었습니다.");

		return response;
	}
}
