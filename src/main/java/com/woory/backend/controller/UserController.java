package com.woory.backend.controller;

import com.woory.backend.dto.UserResponseDto;
import com.woory.backend.repository2.UserRepository;
import com.woory.backend.service.GroupService;
import com.woory.backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public void logout() {

	}
}
