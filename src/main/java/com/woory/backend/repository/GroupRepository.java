package com.woory.backend.Repository;

import com.woory.backend.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {
}
