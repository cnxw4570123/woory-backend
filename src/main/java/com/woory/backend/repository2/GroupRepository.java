package com.woory.backend.repository2;

import com.woory.backend.entity.Group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    void deleteByGroupId(Long groupId);

    boolean existsById(Long groupId);
    Optional<Group> findByGroupId(Long groupId);

}
