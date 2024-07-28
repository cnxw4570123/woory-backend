package com.woory.backend.service;

import com.woory.backend.dto.DataDto;
import com.woory.backend.dto.GroupInfoDto;
import com.woory.backend.dto.MemberDetailDto;
import com.woory.backend.dto.UserDetailDto;
import com.woory.backend.entity.*;
import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;
import com.woory.backend.repository.*;
import com.woory.backend.utils.SecurityUtil;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@Transactional
public class GroupService {
	private UserRepository userRepository;
	private GroupRepository groupRepository;
	private GroupUserRepository groupUserRepository;
	private TopicSetRepository topicSetRepository;
	private ContentRepository contentRepository;
	private ContentReactionRepository contentReactionRepository;
	private CommentRepository commentRepository;
	private final String serverAddress;

	@Autowired
	public GroupService(UserRepository userRepository, GroupRepository groupRepository,
		GroupUserRepository groupUserRepository,
		TopicSetRepository topicSetRepository,
		ContentReactionRepository contentReactionRepository,
		CommentRepository commentRepository,
		ContentRepository contentRepository,
		@Value("${server.ip}") String serverAddress) {
		this.userRepository = userRepository;
		this.groupRepository = groupRepository;
		this.groupUserRepository = groupUserRepository;
		this.topicSetRepository = topicSetRepository;
		this.contentRepository = contentRepository;
		this.commentRepository = commentRepository;
		this.contentReactionRepository = contentReactionRepository;
		this.serverAddress = serverAddress;
	}

	public List<GroupInfoDto> getMyGroups() {
		Long userId = SecurityUtil.getCurrentUserId();
		List<GroupInfoDto> myGroups = groupUserRepository.findMyGroupInfoDto(userId);
		if (myGroups.isEmpty()) {
			throw new CustomException(ErrorCode.USER_GROUPS_NOT_FOUND);
		}
		return myGroups;
	}

	public DataDto getMyGroupId(Long groupID) {
		Long userId = SecurityUtil.getCurrentUserId();

		GroupUser currentGroupUser = groupUserRepository.findGroupUserWithUserByGroupIdAndUserId(userId, groupID)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		User current = currentGroupUser.getUser();
		List<GroupUser> groupUserWithoutUser = groupUserRepository.findGroupUserWithoutUser(groupID, userId);
		UserDetailDto userDetailDto = new UserDetailDto();
		userDetailDto.setUserId(current.getUserId());
		userDetailDto.setUserName(current.getNickname());
		userDetailDto.setProfileUrl(current.getProfileImage());
		userDetailDto.setIsHouseholder(currentGroupUser.getStatus() == GroupStatus.GROUP_LEADER);

		List<MemberDetailDto> memberDTOs = new ArrayList<>();
		for (GroupUser groupUser : groupUserWithoutUser) {
			MemberDetailDto memberDetailDto = new MemberDetailDto();
			User user = groupUser.getUser();
			memberDetailDto.setUserId(user.getUserId());
			memberDetailDto.setUserName(user.getNickname());
			memberDetailDto.setProfileUrl(user.getProfileImage());
			memberDetailDto.setIsHouseholder(groupUser.getStatus() == GroupStatus.GROUP_LEADER);
			memberDTOs.add(memberDetailDto);
		}

		DataDto dataDto = new DataDto();
		dataDto.setUser(userDetailDto);
		dataDto.setMembers(memberDTOs);

		return dataDto;
	}

	public Group createGroup(String groupName, String photoPath) {
		Group group = new Group();
		//로그인된 정보 가져오기

		User byUserId = getUser();
		long cnt = byUserId.getGroupUsers().size();

		if (cnt >= 5) {
			throw new CustomException(ErrorCode.GROUP_CREATION_LIMIT_EXCEEDED);
		}

		group.setGroupName(groupName);
		group.setPhotoPath(photoPath);

		Date now = new Date();

		// GroupUser 생성
		GroupUser groupUser = new GroupUser();
		groupUser.setUser(byUserId);
		groupUser.setGroup(group);
		groupUser.setStatus(GroupStatus.GROUP_LEADER); // 초기 상태 설정
		groupUser.setRegDate(now);
		groupUser.setLastUpdatedDate(new Date());
		// 토픽 생성
		int topicOfToday = TopicManager.getTopicOfToday();
		TopicSet topicSet = topicSetRepository.findTopicSetById((long)topicOfToday)
			.orElseThrow(() -> new CustomException(ErrorCode.TOPIC_NOT_FOUND));
		Topic topic = Topic.fromTopicSetWithDateAndGroup(group, topicSet, now);

		group.setGroupRegDate(LocalDate.ofInstant(now.toInstant(), ZoneId.systemDefault()));
		group.setGroupUsers(List.of(groupUser));
		group.setTopic(List.of(topic));

		return groupRepository.save(group);
	}

	@Transactional
	public void deleteGroup(Long groupId) {
		Long loginId = SecurityUtil.getCurrentUserId();
		//로그인한사람이 그룹장이면 벤이 가능
		GroupStatus status = getGroupStatus(groupId, loginId);
		if (status == GroupStatus.GROUP_LEADER) {
			groupRepository.deleteByGroupId(groupId);
		} else {
			throw new CustomException(ErrorCode.NO_PERMISSION_TO_DELETE_GROUP);
		}
	}

	@Transactional
	public Boolean leaveGroup(Long groupId) {
		//로그인된 정보 가져오기
		Long userId = SecurityUtil.getCurrentUserId();
		List<GroupUser> groupUsers = activeMember(groupId);
		Boolean checkOnePerson = false;
		int cnt = groupUsers.size();
		//1명이하이면 그룹떠날시 그룹 삭제 유저그룹에서 삭제
		if (groupUsers.size() <= 1) {
			groupRepository.deleteByGroupId(groupId);
			return null;
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
			if (status == GroupStatus.MEMBER) {
				groupUserRepository.deleteByGroup_GroupIdAndUser_UserId(groupId, userId);
			}
			cnt--;
			if (cnt == 1) {
				checkOnePerson = true;
			}
		}
		return checkOnePerson;

	}

	public void banGroup(Long groupId, Long userId) {
		Long loginId = SecurityUtil.getCurrentUserId();
		//로그인한사람이 그룹장이면 벤이 가능
		GroupStatus status = getGroupStatus(groupId, loginId);
		if (status != GroupStatus.GROUP_LEADER) {
			throw new CustomException(ErrorCode.NO_PERMISSION_TO_KICK_MEMBER);
		}
		List<Content> contents = contentRepository.findByTopic_Group_GroupId(groupId);
		for (Content content : contents) {
			// 사용자가 작성한 댓글 삭제
			commentRepository.deleteCommentByContent_ContentIdAndUsers_UserId(content.getContentId(), userId);
			// 사용자가 반응한 Reaction 삭제
			contentReactionRepository.deleteContentReactionByContent_ContentIdAndUser_UserId(content.getContentId(), userId);
			// 사용자가 작성한 Content 삭제
			if (content.getUsers().getUserId().equals(userId)) {
				contentRepository.delete(content);
			}
		}
		groupUserRepository.deleteByGroup_GroupIdAndUser_UserId(groupId, userId);
	}

	private GroupStatus getGroupStatus(Long groupId, Long loginId) {
		return groupUserRepository.findByUser_UserIdAndGroup_GroupId(loginId, groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND)).getStatus();
	}

	@Deprecated
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
			throw new CustomException(ErrorCode.GROUP_NOT_FOUND);
		}

		// 로그인된 유저 가져오기
		Long userId = SecurityUtil.getCurrentUserId();
		User byUserId = getUser();
		int userCount = groupUserRepository.findByUser_UserId(userId).size();
		if (userCount >= 5) {
			throw new CustomException(ErrorCode.GROUP_CREATION_LIMIT_EXCEEDED);
		}

		List<GroupUser> byGroupGroupId = groupUserRepository.findAllByGroup_GroupId(groupId);

		boolean present = byGroupGroupId.stream()
			.anyMatch(groupUser -> groupUser.getUser().getUserId().equals(userId));

		if (present) {
			throw new CustomException(ErrorCode.USER_ALREADY_MEMBER);
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
		return userRepository.findByUserIdWithGroupUsers(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	//그룹에 속한 유저인지 아닌지 확인
	public Boolean CheckUserIncludeGroup(Long groupId) {
		Long userId = SecurityUtil.getCurrentUserId();
		Optional<GroupUser> byUserUserIdAndGroupGroupId = groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId,
			groupId);
		if (byUserUserIdAndGroupGroupId.isPresent()) {
			GroupStatus status = byUserUserIdAndGroupGroupId.get().getStatus();
			if (status.equals(GroupStatus.MEMBER) || status.equals(GroupStatus.GROUP_LEADER)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Transactional
	public Group updateGroup(Long groupId, String groupName, String photoPath) {
		Long userId = SecurityUtil.getCurrentUserId();
		GroupStatus status = groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId).get().getStatus();
		if (status == GroupStatus.GROUP_LEADER) {

			Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));

			group.setGroupName(groupName);
			if (photoPath != null) {
				group.setPhotoPath(photoPath); // 사진 경로 수정
			}

			return groupRepository.save(group);
		} else {
			throw new CustomException(ErrorCode.NO_PERMISSION_TO_UPDATE_GROUP); // 권한이 없을 때의 예외 처리
		}

	}

	public List<GroupUser> activeMember(Long groupId) {
		return groupUserRepository.findGroupUsersByGroupIdOrderByRegDate(groupId);
	}

	public GroupInfoDto getGroupInfo(long groupId) {
		Group group = groupRepository.findByGroupId(groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));
		return GroupInfoDto.fromGroup(group);
	}

}
