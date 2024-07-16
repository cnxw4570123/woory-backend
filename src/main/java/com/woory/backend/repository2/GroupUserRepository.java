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

    @Modifying
    @Query("UPDATE GroupUser gu SET gu.status = :newStatus WHERE gu.group.groupId = :groupId AND gu.user.userId = :userId")
    void updateStatusByGroup_GroupIdAndUser_UserId(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("newStatus") GroupStatus newStatus);
    @Query("select gu from GroupUser gu where gu.group.groupId = :groupId and gu.status != com.woory.backend.entity.GroupStatus.BANNED order by gu.regDate asc")
    List<GroupUser> findActiveGroupUsersByGroupIdOrderByRegDate(@Param("groupId") Long groupId);
    Optional<GroupUser> findByUser_UserIdAndGroup_GroupId(Long userId, Long groupId);
    List<GroupUser> findAllByGroup_GroupId(Long groupId);

    void deleteByGroup_GroupIdAndUser_UserId( Long groupId,Long userId);
    void deleteByGroup_GroupId(Long groupId);


    boolean existsByGroup_GroupId(Long groupId);
}
