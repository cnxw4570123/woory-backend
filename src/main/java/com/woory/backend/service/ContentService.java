package com.woory.backend.service;

import com.woory.backend.entity.*;
import com.woory.backend.repository2.*;
import com.woory.backend.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ContentService {

    private ContentRepository contentRepository;
    private UserRepository userRepository;
    private GroupUserRepository groupUserRepository;
    private TopicRepository topicRepository;

    @Autowired
    public ContentService(UserRepository userRepository, GroupRepository groupRepository,
                          ContentRepository contentRepository,GroupUserRepository groupUserRepository,
                          TopicRepository topicRepository) {
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
        this.groupUserRepository = groupUserRepository;
        this.topicRepository = topicRepository;

    }
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
            throw new RuntimeException("컨텐츠를 삭제할 권한이 없습니다.");
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
    public List<Content> getContentBySpecificDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Set the start of the day (00:00:00)
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date startDate = calendar.getTime();

        // Set the end of the day (23:59:59)
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Date endDate = calendar.getTime();

        return contentRepository.findAllByContentRegDateBetween(startDate, endDate);
    }


    private GroupStatus getGroupStatus(Long userId, Long groupId) {
        GroupStatus status = groupUserRepository.findByUser_UserIdAndGroup_GroupId(userId, groupId).get().getStatus();
        return status;
    }

}
