package com.woory.backend.dto;

import com.woory.backend.entity.GroupStatus;
import com.woory.backend.entity.GroupUser;
import com.woory.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserMyPageResponseDto {

    private long userId;
    private String nickname;
    private String profileImgLink;
    private boolean IsHouseholder;
    private boolean IsLastMember;

    public static com.woory.backend.dto.UserMyPageResponseDto fromUserWithCurrentGroup(User user, GroupUser groupUser , boolean check) {
        return com.woory.backend.dto.UserMyPageResponseDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImgLink(user.getProfileImage())
                .IsHouseholder(groupUser.getStatus().equals(GroupStatus.GROUP_LEADER))
                .IsLastMember(check)
                .build();
    }

    public static com.woory.backend.dto.UserMyPageResponseDto fromUser(User user) {
        return com.woory.backend.dto.UserMyPageResponseDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .profileImgLink(user.getProfileImage())
                .build();
    }

}
