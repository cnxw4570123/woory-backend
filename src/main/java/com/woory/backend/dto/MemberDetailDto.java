package com.woory.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MemberDetailDto {
    private Long userId;
    private String userName;
    private String profileUrl;
    @JsonProperty("isHouseholder")
    private boolean isHouseholder;
}
