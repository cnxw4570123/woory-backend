package com.woory.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupRequestDto {
    private Long groupId;
    private String groupName;
    private String photoPath;
}
