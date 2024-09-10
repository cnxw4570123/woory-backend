package com.woory.backend.dto;

import com.woory.backend.entity.GroupStatus;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDto {
	private Long groupId;
    private String email;
    private String groupName;
    private GroupStatus status;
    private LocalDateTime regDate;
    private LocalDateTime lastUpdatedDate;

}
