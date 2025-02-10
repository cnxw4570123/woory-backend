package com.woory.backend.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.woory.backend.config.SecurityConfig;
import com.woory.backend.dto.CustomOAuth2User;
import com.woory.backend.dto.UserDto;
import com.woory.backend.utils.JWTUtil;
import com.woory.backend.utils.JsonUtil;
import com.woory.backend.utils.SecurityUtil;

@WebMvcTest(
	controllers = {AuthController.class},
	excludeFilters = {
		@ComponentScan.Filter(
			type = FilterType.ASSIGNABLE_TYPE,
			classes = SecurityConfig.class
		)
	})
public class AuthControllerTest {
	@MockBean
	JWTUtil jwtUtil;
	@Autowired
	private MockMvc mvc;
	@Autowired
	private WebApplicationContext context;

	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context)
			.build();
	}

	@ParameterizedTest
	@DisplayName("Auth 테스트 - code 올바르지 않을 때")
	@ValueSource(strings = {"kakao", "naver"})
	public void test(String client) throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/auth/" + client)
				.param("code", "some-text")
			)
			.andExpect(MockMvcResultMatchers.status().isBadRequest())
			.andDo(print());
	}

	@ParameterizedTest
	@DisplayName("Auth 테스트 - code가 올바를 때")
	@ValueSource(strings = {"kakao", "naver"})
	public void test1(String client) throws Exception {
		// given
		String code = "test";
		UserDto user = UserDto
			.builder().userId(1L)
			.username(client + "-aaa")
			.role("ROLE_USER")
			.nickname("test")
			.build();

		String accessToken = "testToken.test.test";

		given(jwtUtil.generateAccessToken(anyLong(), anyString()))
			.willReturn(accessToken);

		try (MockedStatic<SecurityUtil> securityUtil = Mockito.mockStatic(SecurityUtil.class)) {
			// when + then
			given(SecurityUtil.getUserByCode(code)).willReturn(new CustomOAuth2User(user));
			mvc.perform(MockMvcRequestBuilders.get("/auth/" + client)
					.param("code", "test")
				)
				.andExpect(MockMvcResultMatchers
					.content().json(JsonUtil.toJson(Map.of("accessToken", accessToken))
					))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(print());

		}

	}
}
