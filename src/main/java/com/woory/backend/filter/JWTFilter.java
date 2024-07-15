package com.woory.backend.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.woory.backend.domain.TokenStatus;
import com.woory.backend.dto.CustomOAuth2User;
import com.woory.backend.dto.UserDto;
import com.woory.backend.utils.JWTUtil;

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
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		Optional<Cookie[]> cookies = Optional.ofNullable(request.getCookies());
		Optional<String> accessToken = Arrays.stream(cookies.orElseGet(() -> new Cookie[] {}))
			.filter(cookie -> cookie.getName().equals("AccessToken"))
			.map(Cookie::getValue)
			.findFirst();

		// AccessToken이 없으면 종료
		if(accessToken.isEmpty()){
			log.info("토큰 없음");
			filterChain.doFilter(request, response);
			return;
		}
		String accToken = accessToken.get();
		if (jwtUtil.validateAccessToken(accToken) == TokenStatus.IS_EXPIRED) {
			filterChain.doFilter(request, response);
			return;
		}

		long userId = jwtUtil.getUserId(accToken);
		List<? extends GrantedAuthority> authorities = jwtUtil.getAuthorities(accToken);

		UserDto us = UserDto.builder()
			.userId(userId)
			.build();
		CustomOAuth2User customOAuth2User = new CustomOAuth2User(us);
		SecurityContextHolder.getContext()
			.setAuthentication(new UsernamePasswordAuthenticationToken(customOAuth2User, null, authorities));
		filterChain.doFilter(request, response);
	}
}
