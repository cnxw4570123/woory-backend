package com.woory.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
public class TopicRequestDto {
    private Long topicId;
    private String topicContent;
    private Date issueDate;
    private int topicByte;
    private GroupRequestDto group;
}
