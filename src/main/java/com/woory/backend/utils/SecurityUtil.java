package com.woory.backend.utils;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.woory.backend.dto.CustomOAuth2User;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@Component
public class SecurityUtil {
	private static HashMap<String, CustomOAuth2User> uuidToUser = new HashMap<>();

	public static Long getCurrentUserId() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		CustomOAuth2User oAuth2User;
		if (authentication == null || (oAuth2User = (CustomOAuth2User)authentication.getPrincipal()) == null) {
			throw new RuntimeException("인증 정보가 없습니다.");
		}
		return Long.valueOf(oAuth2User.getName());
	}

	public static void saveUserWithUUID(UUID uuid, CustomOAuth2User user) {
		uuidToUser.put(uuid.toString(), user);
	}

	public static CustomOAuth2User getUserByCode(String code) {
		return uuidToUser.get(code);
	}
}
