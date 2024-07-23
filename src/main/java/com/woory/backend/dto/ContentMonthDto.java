package com.woory.backend.dto;

public class ContentMonthDto {
    private Long contentId;
    private Long topicId;
    private String contentImgPath;
    private String contentRegDate; // YYYY-MM-DD 형식으로
    private String contentText;
    private Long userId;

    // Getters and Setters

    // Constructor
    public ContentMonthDto(Long contentId, Long topicId, String contentImgPath,
                           String contentRegDate, String contentText, Long userId) {
        this.contentId = contentId;
        this.topicId = topicId;
        this.contentImgPath = contentImgPath;
        this.contentRegDate = contentRegDate;
        this.contentText = contentText;
        this.userId = userId;
    }
}
