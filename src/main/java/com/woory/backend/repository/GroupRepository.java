package com.woory.backend.repository;

import com.woory.backend.entity.Group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    void deleteByGroupId(Long groupId);

    boolean existsById(Long groupId);
    Optional<Group> findByGroupId(Long groupId);
    @Query("SELECT g FROM Group g JOIN g.topic t WHERE t.topicId = :topicId")
    Optional<Group> findByTopic_TopicId(@Param("topicId") Long topicId);

}
