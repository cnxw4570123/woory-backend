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
}
