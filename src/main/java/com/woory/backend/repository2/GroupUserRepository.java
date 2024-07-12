package com.woory.backend.repository2;

import com.woory.backend.entity.GroupStatus;
import com.woory.backend.entity.GroupUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {
    long countByUser_UserId(Long userId);
    void deleteByGroup_GroupId(Long groupId);
    GroupUser findFirstByOrderByRegDateDesc();
    Optional<GroupUser> findByUser_UserIdAndGroup_GroupId(Long userId, Long groupId);
    void deleteByGroup_GroupIdAndUser_UserId(Long userId, Long groupId);
    @Modifying
    @Transactional
    @Query("UPDATE GroupUser gu SET gu.status = :newStatus WHERE gu.group.groupId = :groupId AND gu.user.userId = :userId")
    void updateStatusByGroupIdAndUserId(Long groupId, Long userId, Long status);
}
