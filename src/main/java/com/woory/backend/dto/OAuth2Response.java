package com.woory.backend.dto;

public interface OAuth2Response {
    String getProvider(); //제공자 naver,google
    String getProviderId(); //제공자에서 발급해주는 아이디 비밀번호
    String getEmail(); //이메일
    String getName(); //사용자 설명(설정한이름)
    String getProfileImage();
}
