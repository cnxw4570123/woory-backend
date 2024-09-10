package com.woory.backend.repository;

import com.woory.backend.dto.GroupInfoDto;
import com.woory.backend.entity.GroupStatus;
import com.woory.backend.entity.GroupUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {
	@Modifying(clearAutomatically = true)
	@Query("UPDATE GroupUser gu SET gu.status = :newStatus WHERE gu.group.groupId = :groupId AND gu.user.userId = :userId")
	void updateStatusByGroup_GroupIdAndUser_UserId(@Param("groupId") Long groupId, @Param("userId") Long userId,
		@Param("newStatus") GroupStatus newStatus);

	@Modifying(clearAutomatically = true)
	@Query("delete from GroupUser gu where gu.user.userId = :userId")
	void deleteGroupUsersAssociatedWithUser(@Param("userId") Long userId);

	@Query("select gu from GroupUser gu where gu.group.groupId = :groupId order by gu.regDate asc")
	List<GroupUser> findGroupUsersByGroupIdOrderByRegDate(@Param("groupId") Long groupId);

	// 그룹 유저에서 유저 아이디로 검색 후 그룹까지 조회
	@Query("select new com.woory.backend.dto.GroupInfoDto(g.groupId, g.groupName, g.photoPath, gu.status) from GroupUser gu join gu.group g on g = gu.group and gu.user.userId = :userId order by gu.regDate asc")
	List<GroupInfoDto> findMyGroupInfoDto(@Param("userId") Long userId);

	Optional<GroupUser> findByUser_UserIdAndGroup_GroupId(Long userId, Long groupId);

	List<GroupUser> findAllByGroup_GroupId(Long groupId);

	List<GroupUser> findByUser_UserId(Long userId);

	void deleteByGroup_GroupIdAndUser_UserId(Long groupId, Long userId);

	@Query("select gu from GroupUser gu where gu.group.groupId = :groupId and gu.user.userId != :userId")
	List<GroupUser> findGroupUserWithoutUser(@Param("groupId") Long groupId, @Param("userId") Long userId);

	@Query("select gu from GroupUser gu join fetch gu.user u where u.userId = :userId and gu.group.groupId = :groupId")
	Optional<GroupUser> findGroupUserWithUserByGroupIdAndUserId(
		@Param("userId") long userId,
		@Param("groupId") long groupId);
}
