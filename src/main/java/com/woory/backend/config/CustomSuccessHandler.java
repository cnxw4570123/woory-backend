package com.woory.backend.config;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.woory.backend.dto.CustomOAuth2User;
import com.woory.backend.utils.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JWTUtil jwtUtil;

	public CustomSuccessHandler(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authentication) throws IOException, ServletException {
		CustomOAuth2User user = (CustomOAuth2User)authentication.getPrincipal();

		String username = user.getUsername();
		String authorities = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		String accessToken = jwtUtil.generateAccessToken(username, authorities);
		ResponseCookie cookie = createAccessTokenCookie(accessToken, jwtUtil.getAccTokenExpireTime());
		response.setHeader("set-cookie", cookie.toString());
	}


	private ResponseCookie createAccessTokenCookie(String accessToken, long expiresIn){
		return ResponseCookie.from("AccessToken", accessToken)
			.httpOnly(true)
			.maxAge(expiresIn)
			.path("/")
			.build();
	}


}
