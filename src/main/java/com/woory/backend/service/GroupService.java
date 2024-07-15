package com.woory.backend.service;

import com.woory.backend.dto.CustomOAuth2User;
import com.woory.backend.entity.Group;
import com.woory.backend.entity.GroupStatus;
import com.woory.backend.entity.GroupUser;
import com.woory.backend.entity.User;
import com.woory.backend.repository2.GroupRepository;
import com.woory.backend.repository2.GroupUserRepository;
import com.woory.backend.repository2.UserRepository;
import com.woory.backend.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class GroupService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupUserRepository groupUserRepository;



    public Group createGroup(String groupName,String photoPath) {
        Group group = new Group();
        //로그인된 정보 가져오기
        Long userId = SecurityUtil.getCurrentUserId();
        User byUserId = userRepository.findByUserId(userId).get();
        long cnt = groupUserRepository.countByUser_UserId(userId);
        long count = groupRepository.count();

        if (userId == null) {
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
        group.setPhotoPath(photoPath);


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
    @Transactional
    public void deleteGroup(Long groupId) {
        Long loginId = SecurityUtil.getCurrentUserId();
        //로그인한사람이 그룹장이면 벤이 가능
        GroupStatus status = groupUserRepository.findByUser_UserIdAndGroup_GroupId(loginId, groupId).get().getStatus();
        if (status == GroupStatus.GROUP_LEADER) {
            groupRepository.deleteByGroupId(groupId);
            groupUserRepository.deleteByGroup_GroupId(groupId);
        }
    }

    @Transactional
    public void leaveGroup(Long groupId) {
        //로그인된 정보 가져오기
        Long userId = SecurityUtil.getCurrentUserId();
        long cnt = groupUserRepository.countByGroup_GroupId(groupId);
        //1명이하이면 그룹떠날시 그룹 삭제 유저그룹에서 삭제
        if (cnt <= 1){
            groupRepository.deleteByGroupId(groupId);
            groupUserRepository.deleteByGroup_GroupIdAndUser_UserId(userId,groupId);
        }else{
            GroupStatus status = groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId).get().getStatus();
            if (status == GroupStatus.GROUP_LEADER) {
                GroupUser old = groupUserRepository.findOldestActiveUser();
                old.setStatus(GroupStatus.GROUP_LEADER);
                groupUserRepository.updateStatusByGroup_GroupIdAndUser_UserId(old.getUser().getUserId(),groupId,old.getStatus());
                groupUserRepository.deleteByGroup_GroupIdAndUser_UserId(groupId,userId);
            }
        }


    }

    public void banGroup(Long groupId,Long userId) {
        Long loginId = SecurityUtil.getCurrentUserId();
        //로그인한사람이 그룹장이면 벤이 가능
        GroupStatus status = groupUserRepository.findByUser_UserIdAndGroup_GroupId(loginId, groupId).get().getStatus();
        if (status == GroupStatus.GROUP_LEADER) {
            groupUserRepository.updateStatusByGroup_GroupIdAndUser_UserId(groupId,userId,GroupStatus.BANNED);

        }
    }

    public String generateInviteLink(Long groupId) {
        //로그인한 유저
        Long userId = SecurityUtil.getCurrentUserId();
        GroupStatus statusL = groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId).get().getStatus();
        if (statusL == GroupStatus.GROUP_LEADER) {
            return "http://localhost:8080/api/groups/url/" + groupId ;
        }
        return "";


    }

    @Transactional
    public void joinGroup(Long groupId) {
        // 로그인된 유저 가져오기
        Long userId = SecurityUtil.getCurrentUserId();
        User byUserId = userRepository.findByUserId(userId).get();
        Group byGroupId = groupRepository.findByGroupId(groupId);
        GroupStatus status = groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId).get().getStatus();


        if (status != GroupStatus.GROUP_LEADER || status != GroupStatus.BANNED || status != GroupStatus.MEMBER) {
            // 그룹과 사용자가 존재하는지 확인;p[
            Optional<Group> groupOptional = groupRepository.findById(groupId);
            if (!groupOptional.isPresent()) {
                throw new IllegalArgumentException("Invalid group ID");
            }

            // GroupUser 생성 및 저장
            GroupUser groupUser = new GroupUser();
            groupUser.setUser(byUserId);
            groupUser.setGroup(byGroupId);
            groupUser.setStatus(GroupStatus.MEMBER); // 초대 상태 설정
            groupUser.setRegDate(new Date());
            groupUser.setLastUpdatedDate(new Date());

            groupUserRepository.save(groupUser);
        }


    }

    @Transactional
    public Group updateGroup(Long groupId, String groupName, String photoPath) {
        Long userId = SecurityUtil.getCurrentUserId();
        GroupStatus status = groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId).get().getStatus();
        if (status == GroupStatus.GROUP_LEADER) {

            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("그룹이 존재하지 않습니다."));

            group.setGroupName(groupName);
            if (photoPath != null) {
                group.setPhotoPath(photoPath); // 사진 경로 수정
            }

            return groupRepository.save(group);
        }
        return null;
    }
    public List<GroupUser> activeMember(Long groupId) {
        return groupUserRepository.findActiveUsersByGroup_GroupId(groupId);
    }


}
