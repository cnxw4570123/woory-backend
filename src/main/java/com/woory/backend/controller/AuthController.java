package com.woory.backend.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woory.backend.dto.CustomOAuth2User;
import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;
import com.woory.backend.utils.JWTUtil;
import com.woory.backend.utils.SecurityUtil;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final JWTUtil jwtUtil;

	@GetMapping("/{clientId}")
	public Map<String, Object> auth(@PathVariable("clientId") String clientId, @RequestParam("code") String code,
		HttpServletResponse response) {
		if (!clientId.equals("naver") && !clientId.equals("kakao")) {
			throw new CustomException(ErrorCode.SOCIAL_LOGIN_ERROR);
		}

		CustomOAuth2User userByCode = SecurityUtil.getUserByCode(code);

		if (userByCode == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return Map.of("message", "유효하지 않은 코드입니다.");
		}
		return Map.of("accessToken", issueAccToken(userByCode));
	}

	private String issueAccToken(CustomOAuth2User user) {
		Long userId = Long.valueOf(user.getName());
		String authorities = user.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		return jwtUtil.generateAccessToken(userId, authorities);
	}
}
