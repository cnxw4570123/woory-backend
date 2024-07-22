package com.woory.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class ContentDto {
    private Long contentId;
    private String contentText;
    private String contentImgPath;
    private Date contentRegDate;
    private TopicRequestDto topic;

    public ContentDto(Long contentId, String contentText, String contentImgPath, Date contentRegDate) {
        this.contentId = contentId;
        this.contentText = contentText;
        this.contentImgPath = contentImgPath;
        this.contentRegDate = contentRegDate;
    }
    public ContentDto(){}
}
