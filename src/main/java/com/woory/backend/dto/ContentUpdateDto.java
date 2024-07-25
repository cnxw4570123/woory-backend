package com.woory.backend.dto;

import com.woory.backend.entity.Content;
import com.woory.backend.entity.GroupUser;
import com.woory.backend.entity.Topic;
import com.woory.backend.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentUpdateDto {
    private String topicText;
    private Long contentId;
    private String images;
    private String contentText;


    public static ContentUpdateDto ModifyForm(Topic topic, Content content ) {
        return ContentUpdateDto.builder()
                .topicText(topic.getTopicContent())
                .contentId(content.getContentId())
                .images(content.getContentImgPath())
                .contentText(content.getContentText())
                .build();
    }

}



