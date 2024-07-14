package com.woory.backend.service;

import com.woory.backend.dto.CustomOAuth2User;
import com.woory.backend.entity.Group;
import com.woory.backend.entity.GroupStatus;
import com.woory.backend.entity.GroupUser;
import com.woory.backend.entity.User;
import com.woory.backend.repository2.GroupRepository;
import com.woory.backend.repository2.GroupUserRepository;
import com.woory.backend.repository2.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GroupService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupUserRepository groupUserRepository;



    public Group createGroup(String groupName) {
        Group group = new Group();
        //로그인된 정보 가져오기
        User byUsername = getUser();
        Long id = byUsername.getUserId();
        User byUserId = userRepository.findByUserId(id).get();
        long cnt = groupUserRepository.countByUser_UserId(id);
        long count = groupRepository.count();

        if (id == null) {
            throw new IllegalStateException("User id cannot be null");
        }
        if (cnt >= 5){
            throw new IllegalStateException("User cannot create more than 5 groups");
        }

        // 사용자 추가
        Set<User> users = new HashSet<>();
        users.add(byUserId);
        group.setGroupId(++count);
        group.setGroupName(groupName);

        // 그룹 저장
        group = groupRepository.save(group);
        if (group.getGroupId() == null) {
            throw new IllegalStateException("Group ID was not generated.");
        }

        // GroupUser 생성 및 저장
        GroupUser groupUser = new GroupUser();
        groupUser.setUser(byUserId);
        groupUser.setGroup(group);
        groupUser.setStatus(GroupStatus.GROUP_LEADER); // 초기 상태 설정
        groupUser.setRegDate(new Date());
        groupUser.setLastUpdatedDate(new Date());

        groupUserRepository.save(groupUser);

        return group;

    }
//    public void leaveGroup(Long groupId) {
//        Group group = new Group();
//        //로그인된 정보 가져오기
//        User byUsername = getUser();
//        Long userId = byUsername.getUserId();
//        User byUserId = userRepository.findByUserId(userId);
//        long cnt = groupUserRepository.countByUser_UserId(userId);
//        if (cnt == 1){
//            groupUserRepository.deleteByGroup_GroupId(groupId);
//            groupUserRepository.deleteByGroup_GroupIdAndUser_UserId(userId,groupId);
//        }
//        Optional<GroupUser> groupUserOptional = groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId);
//        GroupUser groupUser = groupUserOptional.get();
//
//        if (groupUser.getStatus() == GroupStatus.GROUP_LEADER) {
//            GroupUser old = groupUserRepository.findFirstByOrderByRegDateDesc();
//            old.setStatus(GroupStatus.GROUP_LEADER);
//            groupUserRepository.updateStatusByGroupIdAndUserId(userId,groupId); //여기가 문제
//            groupUserRepository.deleteByGroup_GroupIdAndUser_UserId(userId,groupId);
//        }
//
//    }

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // principal을 CustomOAuth2User로 캐스팅
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // 이메일 속성 가져오기
        String userName = customOAuth2User.getUsername();
        User byUsername = userRepository.findByUsername(userName).get();
        return byUsername;
    }



}
