package com.woory.backend.controller;

import com.woory.backend.dto.CustomOAuth2User;
import com.woory.backend.entity.Group;
import com.woory.backend.repository2.UserRepository;
import com.woory.backend.service.GroupService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class MyController {

    @Autowired
    GroupService groupService;

    private final UserRepository userRepository;


    public MyController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/my")
    public String myPage(HttpServletRequest request) {
//        // 현재 인증된 사용자 정보 가져오기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        // principal을 CustomOAuth2User로 캐스팅
//        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
//
//        // 이메일 속성 가져오기
//        String email = customOAuth2User.getEmail();
//
//        if (email != null) {
//            return "Logged in as: " + email;
//        } else {
//            return "Email not found";
//        }
        String newgroup = "newgroup";
        groupService.createGroup(newgroup);
        return newgroup;
    }
}
