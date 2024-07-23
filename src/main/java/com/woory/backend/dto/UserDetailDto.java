package com.woory.backend.dto;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDetailDto {
    private Long userId;
    private String userName;
    private String profileUrl;
    private boolean isHouseholder;
}
