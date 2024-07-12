package com.woory.backend.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.web.filter.OncePerRequestFilter;

import com.woory.backend.domain.TokenStatus;
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

		Optional<String> accessToken = Arrays.stream(request.getCookies())
			.filter(cookie -> cookie.getName().equals("AccessToken"))
			.map(Cookie::getValue)
			.findFirst();

		// AccessToken이 없으면 종료
		if(accessToken.isEmpty()){
			log.info("AccessToken is null");
			filterChain.doFilter(request, response);
			return;
		}

		if (jwtUtil.validateAccessToken(accessToken.toString()) == TokenStatus.IS_EXPIRED) {

		}



	}
}
