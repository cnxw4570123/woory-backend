package com.woory.backend.utils;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

	public static ResponseCookie createAccessTokenCookie(String accessToken, long expiresIn) {
		return ResponseCookie.from("AccessToken", accessToken)
			.httpOnly(true)
			.maxAge(expiresIn)
			// .sameSite("None")
			.path("/")
			.build();
	}
}
