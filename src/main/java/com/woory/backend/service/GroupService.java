package com.woory.backend.service;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class GroupService {
	private UserRepository userRepository;
	private GroupRepository groupRepository;
	private GroupUserRepository groupUserRepository;
	private final String serverAddress;

	@Autowired
	public GroupService(UserRepository userRepository, GroupRepository groupRepository,
		GroupUserRepository groupUserRepository,
		@Value("${server.ip}") String serverAddress) {
		this.userRepository = userRepository;
		this.groupRepository = groupRepository;
		this.groupUserRepository = groupUserRepository;
		this.serverAddress = serverAddress;
	}

	public Group createGroup(String groupName, String photoPath) {
		Group group = new Group();
		//로그인된 정보 가져오기

		User byUserId = getUser();
		long cnt = byUserId.getGroups().size();

		if (cnt >= 5) {
			throw new IllegalStateException("User cannot create more than 5 groups");
		}

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
		GroupStatus status = getGroupStatus(groupId, loginId);
		if (status == GroupStatus.GROUP_LEADER) {
			groupRepository.deleteByGroupId(groupId);
			groupUserRepository.deleteByGroup_GroupId(groupId);
		}
	}

	@Transactional
	public void leaveGroup(Long groupId) {
		//로그인된 정보 가져오기
		Long userId = SecurityUtil.getCurrentUserId();
		List<GroupUser> groupUsers = activeMember(groupId);

		//1명이하이면 그룹떠날시 그룹 삭제 유저그룹에서 삭제
		if (groupUsers.size() <= 1) {
			groupUserRepository.deleteByGroup_GroupIdAndUser_UserId(groupId, userId);
			groupRepository.deleteByGroupId(groupId);
		} else {
			GroupStatus status = getGroupStatus(groupId, userId);
			if (status == GroupStatus.GROUP_LEADER) {
				// "가장" 다음으로 오래된 회원
				GroupUser old = groupUsers.get(1);
				old.setStatus(GroupStatus.GROUP_LEADER);
				groupUserRepository.updateStatusByGroup_GroupIdAndUser_UserId(old.getUser().getUserId(), groupId,
						old.getStatus());
				groupUserRepository.deleteByGroup_GroupIdAndUser_UserId(groupId, userId);
			}
		}

	}

	public void banGroup(Long groupId, Long userId) {
		Long loginId = SecurityUtil.getCurrentUserId();
		//로그인한사람이 그룹장이면 벤이 가능
		GroupStatus status = getGroupStatus(groupId, loginId);
		if (status == GroupStatus.GROUP_LEADER) {
			groupUserRepository.updateStatusByGroup_GroupIdAndUser_UserId(groupId, userId, GroupStatus.BANNED);

		}
	}

	private GroupStatus getGroupStatus(Long groupId, Long loginId) {
		GroupStatus status = groupUserRepository.findByUser_UserIdAndGroup_GroupId(loginId, groupId)
			.orElseThrow(() -> new RuntimeException("해당 회원 없음")).getStatus();
		return status;
	}

	public String generateInviteLink(Long groupId) {
		//로그인한 유저
		Long userId = SecurityUtil.getCurrentUserId();
		GroupStatus statusL = getGroupStatus(groupId, userId);
		if (statusL == GroupStatus.GROUP_LEADER) {
			return serverAddress + "/v1/groups/url/" + groupId;
		}
		throw new RuntimeException("권한 없음");
	}

	@Transactional
	public void joinGroup(Long groupId) {
		if (!groupRepository.existsById(groupId)) {
			throw new IllegalArgumentException("Invalid group ID");
		}

		// 로그인된 유저 가져오기
		Long userId = SecurityUtil.getCurrentUserId();
		User byUserId = getUser();

		List<GroupUser> byGroupGroupId = groupUserRepository.findAllByGroup_GroupId(groupId);

		boolean present = byGroupGroupId.stream()
			.anyMatch(groupUser -> groupUser.getUser().getUserId().equals(userId));

		if (present) {
			throw new IllegalStateException("이미 회원 가입 기록 있습니다.");
		}

		// 확실히 그룹에 들어올 수 있음
		// GroupUser 생성 및 저장
		GroupUser groupUser = new GroupUser();
		groupUser.setUser(byUserId);
		groupUser.setGroup(byGroupGroupId.get(0).getGroup());
		groupUser.setStatus(GroupStatus.MEMBER); // 초대 상태 설정
		groupUser.setRegDate(new Date());
		groupUser.setLastUpdatedDate(new Date());

		groupUserRepository.save(groupUser);
	}

	private User getUser() {
		Long userId = SecurityUtil.getCurrentUserId();
		User byUserId = userRepository.findByUserIdWithGroups(userId)
			.orElseThrow(() -> new RuntimeException("회원 정보 없음"));
		return byUserId;
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
		return groupUserRepository.findActiveGroupUsersByGroupIdOrderByRegDate(groupId);
	}

}
