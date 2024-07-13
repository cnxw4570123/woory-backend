package com.woory.backend.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.woory.backend.dto.CustomOAuth2User;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SecurityUtil {
	public static Long getCurrentUsername() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		CustomOAuth2User oAuth2User;
		if (authentication == null || (oAuth2User = (CustomOAuth2User)authentication.getPrincipal()) == null) {
			throw new RuntimeException("인증 정보가 없습니다.");
		}
		return oAuth2User.getUserId();
	}
}
