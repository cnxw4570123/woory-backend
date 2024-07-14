package com.woory.backend.dto;

import com.woory.backend.entity.Group;
import com.woory.backend.entity.GroupStatus;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

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

    // Getters and setters
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public GroupStatus getStatus() {
        return status;
    }

    public void setStatus(GroupStatus status) {
        this.status = status;
    }

    public LocalDateTime getRegDate() {
        return regDate;
    }

    public void setRegDate(LocalDateTime regDate) {
        this.regDate = regDate;
    }

    public LocalDateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public static GroupDto fromGroup(Group group){
        return GroupDto.builder()
            .groupId(group.getGroupId())
            .groupName(group.getGroupName())
            .build();
    }
}
