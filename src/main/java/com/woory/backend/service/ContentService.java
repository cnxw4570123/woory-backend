package com.woory.backend.service;

import com.woory.backend.dto.ContentReactionDto;
import com.woory.backend.entity.*;
import com.woory.backend.repository2.*;
import com.woory.backend.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContentService {

    private ContentRepository contentRepository;
    private UserRepository userRepository;
    private GroupUserRepository groupUserRepository;
    private TopicRepository topicRepository;
    private final ContentReactionRepository contentReactionRepository;

    @Autowired
    public ContentService(UserRepository userRepository, GroupRepository groupRepository,
                          ContentRepository contentRepository,GroupUserRepository groupUserRepository,
                          TopicRepository topicRepository,ContentReactionRepository contentReactionRepository) {
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
        this.groupUserRepository = groupUserRepository;
        this.topicRepository = topicRepository;
        this.contentReactionRepository = contentReactionRepository;

    }
    @Transactional
    public Content createContent(Long groupId, Long topicId, String contentText, String contentImgPath) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId)
                .orElseThrow(()-> new NoSuchElementException("그룹과 유저를 찾을 수 없습니다."));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new NoSuchElementException("토픽을 찾을 수 없습니다."));

        // Content 생성 및 저장 로직
        Content content = new Content();
        content.setContentText(contentText);
        content.setContentImgPath(contentImgPath);
        content.setUsers(user);
        content.setTopic(topic);
        content.setContentRegDate(new Date());

        return contentRepository.save(content);
    }



    @Transactional
    public void deleteContent(Long groupId,Long contentId) {
        Long userId = SecurityUtil.getCurrentUserId();
        GroupStatus status = groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId)
                .orElseThrow(() -> new NoSuchElementException("그룹과 아이디를 찾을 수 없습니다.")).getStatus();
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("컨텐츠를 찾을 수 없습니다."));
        //본인의 것만 삭제하기 위해서
        if (!content.getUsers().getUserId().equals(userId)) {
            throw new RuntimeException("컨텐츠를 삭제할 권한이 없습니다.");
        }
        if (status == GroupStatus.BANNED || status == GroupStatus.NON_MEMBER) {
            throw new RuntimeException("컨텐츠를 삭제할 권한이 없습니다.");
        }
        contentRepository.delete(content);
    }

    @Transactional
    public Content updateContent(Long groupId,Long contentId, String contentText, String contentImg) {
        Long userId = SecurityUtil.getCurrentUserId();

        groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId,groupId)
                .orElseThrow(()-> new NoSuchElementException("그룹과 이름을 찾을 수 없습니다."));
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("컨텐츠를 수정할 수 없습니다."));
        GroupStatus status = getGroupStatus(userId, groupId);

        if (!content.getUsers().getUserId().equals(userId)) {
            throw new RuntimeException("컨텐츠를 수정할 권한이 없습니다.");
        }

        if (status == GroupStatus.BANNED || status == GroupStatus.NON_MEMBER) {
            throw new RuntimeException("컨텐츠를 수정할 권한이 없습니다.");
        }
        content.setContentText(contentText);
        if (contentImg != null) {
            content.setContentImgPath(contentImg); // 사진 경로 수정
        }
        return contentRepository.save(content);


    }
    public List<Content> getContentsByRegDateLike(String dateStr) {
        return contentRepository.findContentsByRegDateLike(dateStr + "%");
    }

    //리엑션 추가
    public ContentReactionDto addOrUpdateReaction(Long contentId, Long userId, ReactionType newReaction) {
        Content content = contentRepository.findByContentId(contentId)
                .orElseThrow(() -> new NoSuchElementException("해당 컨텐츠를 찾을 수 없습니다."));
        ContentReactionId id = new ContentReactionId(contentId, userId);
        Optional<ContentReaction> byId = contentReactionRepository.findById(id);

        if (byId.isPresent()) {
            ContentReaction contentReaction = byId.get();
            if(contentReaction.getReaction() == newReaction){
                removeReaction(contentId, userId);
                return null;
            }
            decreaseReactionCount(content,contentReaction.getReaction());
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을수 없습니다."));
        ContentReaction contentReaction = new ContentReaction(id, content, user, newReaction);
        contentReactionRepository.save(contentReaction);

        increaseReactionCount(content, newReaction);
        contentRepository.save(content);

        ContentReactionDto dto = new ContentReactionDto();
        dto.setContentId(contentReaction.getContent().getContentId());
        dto.setUserId(contentReaction.getUser().getUserId());
        dto.setReaction(contentReaction.getReaction());
        return dto;

    }
    //컨텐츠의 리액션 보기
    public List<ContentReactionDto> getReactionsByContentId(Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));

        List<ContentReaction> reactions = contentReactionRepository.findByContent_ContentId(contentId);

        // Convert List<ContentReaction> to List<ContentReactionDto>
        return reactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ContentReactionDto convertToDto(ContentReaction reaction) {
        ContentReactionDto dto = new ContentReactionDto();
        dto.setContentId(reaction.getContent().getContentId());
        dto.setUserId(reaction.getUser().getUserId());
        dto.setReaction(reaction.getReaction());
        return dto;
    }


    private void removeReaction(Long contentId, Long userId) {
        ContentReactionId id = new ContentReactionId(contentId, userId);
        ContentReaction contentReaction = contentReactionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("리액션을 찾을 수 없습니다."));
        Content content = contentReaction.getContent();
        decreaseReactionCount(content, contentReaction.getReaction());

        contentReactionRepository.delete(contentReaction);

        // Save the content
        contentRepository.save(content);
    }


    private GroupStatus getGroupStatus(Long userId, Long groupId) {
        GroupStatus status = groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId).get().getStatus();
        return status;
    }

    private void increaseReactionCount(Content content, ReactionType reaction) {
        switch (reaction) {
            case LIKE -> content.setLikeCount(content.getLikeCount() + 1);
            case LOVE -> content.setLoveCount(content.getLoveCount() + 1);
            case WOW -> content.setWowCount(content.getWowCount() + 1);
            case SAD -> content.setSadCount(content.getSadCount() + 1);
            case ANGRY -> content.setAngryCount(content.getAngryCount() + 1);
        }
    }

    private void decreaseReactionCount(Content content, ReactionType reaction) {
        switch (reaction) {
            case LIKE -> content.setLikeCount(content.getLikeCount() - 1);
            case LOVE -> content.setLoveCount(content.getLoveCount() - 1);
            case WOW -> content.setWowCount(content.getWowCount() - 1);
            case SAD -> content.setSadCount(content.getSadCount() - 1);
            case ANGRY -> content.setAngryCount(content.getAngryCount() - 1);
        }
    }

}
