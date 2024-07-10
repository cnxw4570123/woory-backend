package com.woory.backend.service;

import com.woory.backend.dto.CustomOAuth2User;
import com.woory.backend.entity.Group;
import com.woory.backend.entity.GroupStatus;
import com.woory.backend.entity.User;
import com.woory.backend.repository2.GroupRepository;
import com.woory.backend.repository2.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GroupService {
    @Autowired
    UserRepository userRepository;

    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Group createGroup(String groupName) {
        Group group = new Group();
        //로그인된 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // principal을 CustomOAuth2User로 캐스팅
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // 이메일 속성 가져오기
        String email = customOAuth2User.getEmail();

        if (email == null) {
            throw new IllegalStateException("User email cannot be null");
        }
        long cnt = groupRepository.countByEmail(email);
        if (cnt < 5){
            User user = (User) userRepository.findByEmail(email);
            group.setEmail(user.getEmail());
            group.setGroupName(groupName);
            group.setStatus(GroupStatus.GROUP_LEADER);
            group.setRegDate(LocalDateTime.now());
            group.setLastUpdatedDate(LocalDateTime.now());
//
            return groupRepository.save(group);
        }else{
            throw new IllegalStateException("User have 5 groups");
        }




    }


}
