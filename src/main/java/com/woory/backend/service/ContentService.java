package com.woory.backend.service;

import com.woory.backend.dto.*;
import com.woory.backend.entity.*;
import com.woory.backend.error.CustomException;
import com.woory.backend.error.ErrorCode;
import com.woory.backend.repository.*;
import com.woory.backend.utils.SecurityUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentService {

	private static final Logger log = LoggerFactory.getLogger(ContentService.class);
	private final TopicSetRepository topicSetRepository;
	private final GroupRepository groupRepository;
	private final ContentRepository contentRepository;
	private final UserRepository userRepository;
	private final GroupUserRepository groupUserRepository;
	private final TopicRepository topicRepository;
	private final ContentReactionRepository contentReactionRepository;
	private final AwsService awsService;
	private final FavoriteRepository favoriteRepository;
	private final NotificationService notificationService;

	public ContentDto getContentById(Long contentId) {
		Content content = contentRepository.findByContentId(contentId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));
		ContentDto contentDto = new ContentDto();
		contentDto.setContentId(content.getContentId());
		contentDto.setContentText(content.getContentText());
		contentDto.setContentImgPath(contentDto.getContentImgPath());
		contentDto.setContentRegDate(content.getContentRegDate());

		return contentDto;
	}

	@Transactional
	public Content createContent(Long groupId, Long topicId, String contentText, String images) {
		Long userId = SecurityUtil.getCurrentUserId();
		User user = userRepository.findByUserIdWithGroupUsers(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));
		Topic topic = topicRepository.findById(topicId)
			.orElseThrow(() -> new CustomException(ErrorCode.TOPIC_NOT_FOUND));

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(topic.getIssueDate());
		LocalDate localDate = calendar.toInstant().atZone(ZoneId.of("Asia/Seoul")).toLocalDate();

		canPostContent(localDate);
		// 사용자가 이미 해당 주제에 콘텐츠를 작성했는지 확인
		boolean userHasContentForTopic = contentRepository.existsByTopic_TopicIdAndUsers_UserId(topicId, userId);
		if (userHasContentForTopic) {
			throw new CustomException(ErrorCode.CONTENT_ALREADY_EXISTS);
		}

		// Content 생성 및 저장 로직
		Content content = new Content();
		content.setContentText(contentText);
		// 사진을 보넀을 경우
		if (!TextUtils.isEmpty(images)) {
			String newPhotoPath = awsService.saveImage(images);
			content.setContentImgPath(newPhotoPath);
		}

		Date now = new Date();
		content.setUsers(user);
		content.setTopic(topic);
		content.setContentRegDate(now);

		Content save = contentRepository.save(content);

		Notification notification = Notification.fromCreatingContent(groupId, user.getUserId(), save.getContentId(),
			now);
		notificationService.storeNotification(notification);

		return save;
	}

	@Transactional
	public void deleteContent(Long groupId, Long contentId) {
		Long userId = SecurityUtil.getCurrentUserId();
		groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_IN_GROUP));

		Content content = contentRepository.findById(contentId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));
		String contentImgPath = content.getContentImgPath();

		//본인의 것만 삭제하기 위해서
		if (!content.getUsers().getUserId().equals(userId)) {
			throw new CustomException(ErrorCode.NO_PERMISSION_TO_DELETE);
		}
		contentRepository.delete(content);
		awsService.deleteImage(contentImgPath);
	}

	@Transactional
	public Content updateContent(Long groupId, Long contentId, String contentText, String newPhoto) {
		Long userId = SecurityUtil.getCurrentUserId();

		groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));

		Content content = contentRepository.findContentWithUserByContentId(contentId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));

		if (!content.getUsers().getUserId().equals(userId)) {
			throw new CustomException(ErrorCode.NO_PERMISSION_TO_UPDATE);
		}
		content.setContentText(contentText);

		String contentImgPath = content.getContentImgPath();

		// images가 null값으로 온다 -> 텍스트만 수정하는 경우
		if (TextUtils.isEmpty(newPhoto)) {
			return contentRepository.save(content);
		}
		// images가 delete로 온다 -> 기존 사진을 삭제하려는 경우
		if (newPhoto.equals("delete")) {
			content.setContentImgPath(null);
			awsService.deleteImage(contentImgPath);
			return contentRepository.save(content);
		}

		// images가 base64 파일로 오는 경우 -> 기존 사진을 수정
		// 혹시 모를 오류가 발생하더라도
		String newPhotoPath = awsService.saveImage(newPhoto);
		content.setContentImgPath(newPhotoPath); // 사진 경로 수정
		Content save = contentRepository.save(content);
		awsService.deleteImage(contentImgPath);

		return save;

	}

	public List<ContentDto> getContentsByRegDateMonthLike(Long groupId, String dateStr) {
		Long currentUserId = SecurityUtil.getCurrentUserId();
		checkUserGroup(groupId, currentUserId);
		List<ContentMonthDto> contents = contentRepository.findByDateWithImgPath(groupId, currentUserId, dateStr + "%");

		Map<String, List<ContentMonthDto>> groupedByDate = contents.stream()
			.collect(Collectors.groupingBy(t -> t.getContentRegDate().toString()));

		List<ContentDto> result = new ArrayList<>();
		for (Map.Entry<String, List<ContentMonthDto>> entry : groupedByDate.entrySet()) {
			List<ContentMonthDto> contentList = entry.getValue();

			Optional<ContentMonthDto> first = contentList.stream()
				.filter(c -> !TextUtils.isEmpty(c.getContentImgPath()))
				.findFirst();

			// 키 값이 있다면 최소 하나의 원소는 있음
			ContentMonthDto contentMonthDto = first.orElseGet(() -> contentList.get(0));

			try {
				result.add(new ContentDto(contentMonthDto.getContentImgPath(),
					new SimpleDateFormat("yyyy-MM-dd").parse(contentMonthDto.getContentRegDate()),
					contentMonthDto.getIsFavorite()));
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	public ContentWithUserAndTopicDto getContent(Long contentId) {
		Long currentUserId = SecurityUtil.getCurrentUserId();
		Content content = contentRepository.findContentWithTopic(contentId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));

		Long groupId = content.getTopic().getGroup().getGroupId();
		checkUserGroup(groupId, currentUserId);
		return ContentWithUserAndTopicDto.fromTopicAndContent(currentUserId, content, content.getTopic());
	}

	public ContentUpdateDto getModifyContentInf(Long contentId) {
		Long currentUserId = SecurityUtil.getCurrentUserId();
		ContentWithUserDto contentWithUserDto = new ContentWithUserDto();
		Content content = contentRepository.findByContentId(contentId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));
		Long topicId = content.getTopic().getTopicId();
		Topic topic = topicRepository.findByTopicId(topicId)
			.orElseThrow(() -> new CustomException(ErrorCode.TOPIC_NOT_FOUND));
		return ContentUpdateDto.ModifyForm(topic, content);
	}

	public ContentReactionDto addOrUpdateReaction(Long contentId, Long userId, ReactionType newReaction) {
		Content content = contentRepository.findContentWithTopic(contentId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));

		Long groupId = content.getTopic().getGroup().getGroupId();

		groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_IN_GROUP));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		Optional<ContentReaction> reaction = contentReactionRepository.findContentReactionByContent_ContentIdAndUser_UserId(
			contentId, userId);

		// 리액션을 한 번도 누르지 않았다면
		if (reaction.isEmpty()) {
			ContentReaction contentReaction = new ContentReaction(content, user, newReaction);
			ContentReaction save = contentReactionRepository.save(contentReaction);

			// 본인 게시글이 아닌 경우에만
			if (!userId.equals(content.getUsers().getUserId())) {
				Notification notification = Notification.fromCreatingEmoji(groupId, contentId, userId, save.getId(),
					content.getUsers().getUserId(), new Date());
				notificationService.storeNotification(notification);
			}

			return ContentReactionDto.toContentReactionDto(contentReaction);
		}
		// 이미 누른 것
		ContentReaction contentReaction = reaction.get();
		// 같은 리액션 또 누르면 -> 삭제
		if (contentReaction.getReaction().equals(newReaction)) {
			contentReactionRepository.delete(contentReaction);
			return null;
		}

		contentReaction.setReaction(newReaction);
		return ContentReactionDto.toContentReactionDto(contentReactionRepository.save(contentReaction));
	}

	//컨텐츠의 리액션 보기
	public List<ContentReactionDto.ForStatistics> getReactionsByContentId(Long contentId) {
		Content content = contentRepository.findByContentId(contentId)
			.orElseThrow(() -> new CustomException(ErrorCode.CONTENT_NOT_FOUND));

		Long groupId = content.getTopic().getGroup().getGroupId();

		groupUserRepository.findByUser_UserIdAndGroup_GroupId(SecurityUtil.getCurrentUserId(), groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_IN_GROUP));

		List<ContentReaction> reactions = contentReactionRepository.findByContentIdWithUser(contentId);

		// Convert List<ContentReaction> to List<ContentReactionDto>
		return ContentReactionDto.toReactionForStatistics(SecurityUtil.getCurrentUserId(), reactions);
	}

	public TopicDto getTopicWithContents(LocalDate date, Long groupId) {
		log.info("date = {}", date.toString());
		Long currentUserId = SecurityUtil.getCurrentUserId();
		GroupUser groupUser = checkUserGroup(groupId, currentUserId);
		Group group = groupRepository.findByGroupId(groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));
		// 그룹 생성일 이전이라면
		if (group.getGroupRegDate().isAfter(date)) {
			throw new CustomException(ErrorCode.CAN_NOT_VIEW_BEFORE_GROUP_REG_DATE);
		}

		Topic topic = topicRepository.findTopicByGroupIdAndIssueDateWithContent(
			groupId, date).orElseGet(() -> {
			TopicSet topicSet = topicSetRepository.findRandomTopic();
			Topic topic2 = Topic.fromTopicSetWithDateAndGroup(group, topicSet, java.sql.Date.valueOf(date));
			return topicRepository.save(topic2);
		});

		boolean hasPrevDay = topicRepository.existsByGroupRegDate(groupId, date);
		boolean hasNextDay
			= topicRepository.existsByGroup_GroupIdAndAndIssueDate(groupId, date.plusDays(1L));
		boolean isFavorite = favoriteRepository.existsByTopicAndGroupUser(topic, groupUser);
		return TopicDto.fromTopicWithContents(SecurityUtil.getCurrentUserId(), topic, hasPrevDay, hasNextDay,
			isFavorite);
	}

	private GroupUser checkUserGroup(Long groupId, Long currentUserId) {
		return groupUserRepository.findByUser_UserIdAndGroup_GroupId(currentUserId, groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_IN_GROUP));
	}

	public TopicDto getTopicOnly(LocalDate date, Long groupId) {
		Topic topic = topicRepository.findTopicByGroupIdAndIssueDate(groupId, date)
			.orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));
		return TopicDto.fromTopic(topic);
	}

	private void canPostContent(LocalDate issueDate) {
		LocalDate today = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDate();
		if (today.isAfter(issueDate)) {
			throw new CustomException(ErrorCode.CAN_NOT_POST_AFTER_DAY);
		}
	}

	public List<FavoriteDto> getFavorites(Long groupId) {
		Long userId = SecurityUtil.getCurrentUserId();
		GroupUser groupUser = groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_GROUPS_NOT_FOUND));

		List<Topic> topics = favoriteRepository.findAllWithTopicByGroupUser(groupUser)
			.stream()
			.map(Favorite::getTopic)
			.toList();

		List<Topic> topicWithContents = topicRepository.findAllWithContentsByTopics(topics);

		List<FavoriteDto> result = new ArrayList<>();
		for (Topic t : topicWithContents) {
			String contentImg = t.getContent().stream()
				.map(Content::getContentImgPath)
				.filter(Objects::nonNull).findFirst()
				.orElseGet(() -> null);

			result.add(new FavoriteDto(t.getTopicId(), t.getIssueDate(), contentImg, t.getTopicContent()));
		}

		return result;
	}

	public void addOrDeleteHeart(Long groupId, Long topicId) {
		Long userId = SecurityUtil.getCurrentUserId();
		GroupUser groupUser = groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND_IN_GROUP));

		Topic topic = topicRepository.findTopicWithContentsByTopicId(topicId)
			.orElseThrow(() -> new CustomException(ErrorCode.TOPIC_NOT_FOUND));

		// 만약 존재하면 지움.
		if (favoriteRepository.existsByTopicAndGroupUser(topic, groupUser)) {
			favoriteRepository.deleteFavoriteByTopicAndGroupUser(topic, groupUser);
			return;
		}

		Favorite fav = Favorite.builder()
			.topic(topic)
			.groupUser(groupUser)
			.groupId(groupId)
			.build();

		favoriteRepository.save(fav);
	}
}
