package com.woory.backend.repository2;

import com.woory.backend.entity.Group;
import com.woory.backend.entity.GroupStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    public Group findByEmail(String email);
    long countByUserId(Long userId);
    long deleteByEmailAndStatus(String email, GroupStatus status);
    List<Group> findByEmailAndStatus(String email, GroupStatus status);
}
