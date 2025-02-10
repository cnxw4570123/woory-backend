package com.woory.backend.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.security.StaticResourceLocation;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woory.backend.domain.TokenStatus;
import com.woory.backend.dto.CustomOAuth2User;
import com.woory.backend.dto.UserDto;
import com.woory.backend.utils.JWTUtil;
import com.woory.backend.utils.JsonUtil;
import com.woory.backend.utils.SecurityUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JWTFilter extends OncePerRequestFilter {

	private final JWTUtil jwtUtil;

	public JWTFilter(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String requestURI = request.getRequestURI();
		log.info("path = {}", requestURI);
		return !requestURI.matches("^v1/.*");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String accessToken = request.getHeader("Authorization");

		// AccessToken이 없으면 기존 로그인 로직 수행
		if (!StringUtils.hasText(accessToken)) {
			log.info("토큰 없음");
			filterChain.doFilter(request, response);
			return;
		}
		accessToken = accessToken.split("Bearer ")[1];
		// 이미 AccessToken 발급된 상황
		// Authorization => 헤더가 있으면 Bearer ${AccessToken} 검증
		if (jwtUtil.validateAccessToken(accessToken) == TokenStatus.IS_EXPIRED) {
			filterChain.doFilter(request, response);
			return;
		}

		long userId = jwtUtil.getUserId(accessToken);
		List<? extends GrantedAuthority> authorities = jwtUtil.getAuthorities(accessToken);

		UserDto us = UserDto.builder()
			.userId(userId)
			.build();
		CustomOAuth2User customOAuth2User = new CustomOAuth2User(us);
		SecurityContextHolder.getContext()
			.setAuthentication(new UsernamePasswordAuthenticationToken(customOAuth2User, null, authorities));
		filterChain.doFilter(request, response);
	}
}
