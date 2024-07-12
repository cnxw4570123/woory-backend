package com.woory.backend.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {
	private final Long id;

	private final Map<String, Object> attributes;

	public KakaoResponse(Map<String, Object> attributes) {
		this.id = (Long)attributes.get("id");
		this.attributes = (Map<String, Object>)attributes.get("kakao_account");;
	}

	@Override
	public String getProvider() {
		return "kakao";
	}

	@Override
	public String getProviderId() {
		return Long.toString(id);
	}

	@Override
	public String getEmail() {
		return attributes.get("email").toString();
	}

	@Override
	public String getName() {
		return getProfile().get("nickname").toString();
	}

	private Map<String, Object> getProfile() {
		return (Map<String, Object>)attributes.get("profile");
	}

	public String getProfileImage() {
		return getProfile().get("profile_image_url").toString();
	}
}
