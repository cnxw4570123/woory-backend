package com.woory.backend.repository2;

import com.woory.backend.entity.Group;
import com.woory.backend.entity.GroupStatus;
import com.woory.backend.entity.GroupUser;
import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    @Transactional
    void deleteByGroupId(Long groupId);

    Group findByGroupId(Long groupId);

}
