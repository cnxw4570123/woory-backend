package com.woory.backend.config;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.woory.backend.dto.CustomOAuth2User;
import com.woory.backend.utils.SecurityUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		CustomOAuth2User user = (CustomOAuth2User)authentication.getPrincipal();

		UUID code = UUID.randomUUID();
		SecurityUtil.saveUserWithUUID(code, user);

		String client = user.getUsername().split(" ")[0];

		response.sendRedirect("https://woory.vercel.app/oauth/callback/" + client + "?code=" + code);
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
