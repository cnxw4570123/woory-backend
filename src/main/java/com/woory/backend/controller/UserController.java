package com.woory.backend.controller;

import com.woory.backend.dto.UserResponseDto;
import com.woory.backend.service.GroupService;
import com.woory.backend.service.UserService;
import com.woory.backend.utils.CookieUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("v1/users")
@AllArgsConstructor
public class UserController {
	private GroupService groupService;
	private UserService userService;

	@GetMapping("/my")
	public ResponseEntity<UserResponseDto> my() {
		return ResponseEntity.ok(userService.getUserInfo());
	}

	@GetMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletResponse response) {
		ResponseCookie cookie = CookieUtil.createAccessTokenCookie("", 0);
		response.setHeader("Set-Cookie", cookie.toString());
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
