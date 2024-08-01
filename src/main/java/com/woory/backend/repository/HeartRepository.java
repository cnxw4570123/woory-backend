package com.woory.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.woory.backend.entity.Favorite;

public interface HeartRepository extends JpaRepository<Favorite, Long> {
}
