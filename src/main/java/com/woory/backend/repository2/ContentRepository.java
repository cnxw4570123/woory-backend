package com.woory.backend.repository2;

import com.woory.backend.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findAllByContentRegDateBetween(Date startDate, Date endDate);
    Optional<Content> findByContentId(Long contentId);

    @Query(value = "SELECT * FROM content WHERE DATE_FORMAT(content_reg_date, '%Y-%m-%d') LIKE :date%", nativeQuery = true)
    List<Content> findContentsByRegDateLike(@Param("date") String date);
}
