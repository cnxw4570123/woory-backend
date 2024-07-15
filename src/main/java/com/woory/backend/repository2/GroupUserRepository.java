package com.woory.backend.repository2;

import com.woory.backend.entity.GroupStatus;
import com.woory.backend.entity.GroupUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {

    long countByGroup_GroupId(Long groupId);
    long countByUser_UserId(Long userId);

    @Query(value = "SELECT * FROM Group_User WHERE status = 1 ORDER BY reg_date ASC LIMIT 1", nativeQuery = true)
    GroupUser findOldestActiveUser();
    Optional<GroupUser> findByUser_UserIdAndGroup_GroupId(Long userId, Long groupId);
    List<GroupUser> findByGroup_GroupId(Long groupId);
    @Query(value = "SELECT * FROM group_user gu WHERE gu.status = 1 AND gu.groupId = :groupId", nativeQuery = true)
    List<GroupUser> findActiveUsersByGroup_GroupId(@Param("groupId") Long groupId);

    void deleteByGroup_GroupIdAndUser_UserId( Long groupId,Long userId);
    @Transactional
    void deleteByGroup_GroupId(Long groupId);

    @Modifying
    @Transactional
    @Query("UPDATE GroupUser gu SET gu.status = :newStatus WHERE gu.group.groupId = :groupId AND gu.user.userId = :userId")
    void updateStatusByGroup_GroupIdAndUser_UserId(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("newStatus") GroupStatus newStatus);

    boolean existsByGroup_GroupId(Long groupId);
}
