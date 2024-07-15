package com.woory.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woory.backend.dto.UserResponseDto;
import com.woory.backend.entity.User;
import com.woory.backend.repository2.UserRepository;
import com.woory.backend.utils.SecurityUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class UserService {
	private UserRepository userRepository;

	public UserResponseDto getUserInfo() {
		Long userId = SecurityUtil.getCurrentUserId();

		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new RuntimeException("회원 정보 없음"));

		return UserResponseDto.fromUser(user);
	}

	public void deleteAccount(){
		Long userId = SecurityUtil.getCurrentUserId();

		User user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new RuntimeException("회원 정보 없음"));

		if (user.getUsername().startsWith("kakao")){

		}

	}

}
