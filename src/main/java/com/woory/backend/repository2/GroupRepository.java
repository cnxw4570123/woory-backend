package com.woory.backend.repository2;

import com.woory.backend.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
    public Group findByEmail(String email);
    long countByEmail(String email);
}
