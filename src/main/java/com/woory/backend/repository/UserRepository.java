package com.woory.backend.repository;

import java.util.Optional;

import com.woory.backend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	@Query("select u from User u left join fetch u.groups where u.userId = :userId")
	Optional<User> findByUserIdWithGroups(@Param("userId") long id);
}
