package com.woory.backend.repository2;

import com.woory.backend.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findAllByContentRegDateBetween(Date startDate, Date endDate);
}
