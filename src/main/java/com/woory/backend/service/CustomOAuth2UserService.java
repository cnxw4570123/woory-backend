package com.woory.backend.service;

import com.woory.backend.dto.CustomOAuth2User;
import com.woory.backend.dto.KakaoResponse;
import com.woory.backend.dto.NaverResponse;
import com.woory.backend.dto.OAuth2Response;
import com.woory.backend.entity.User;
import com.woory.backend.repository2.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("oAuth2User = " + oAuth2User.getAttributes());

        String registration = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registration.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registration.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }
        else{
            return null;
        }
        String role = null;
        //구현
        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
        User existData = userRepository.findByUsername(username);
        if (existData == null) {
            User userEntity = new User();
            userEntity.setUsername(username);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setProfileImage(oAuth2Response.getProfileImage());
            userEntity.setRole("ROLE_USER");
            userRepository.save(userEntity);
        }else {
            existData.setUsername(username);
            existData.setEmail(oAuth2Response.getEmail());
            existData.setProfileImage(oAuth2Response.getProfileImage());
            role =existData.getRole();
            userRepository.save(existData);
        }



        return new CustomOAuth2User(oAuth2Response , role);
    }



}
