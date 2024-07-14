package com.woory.backend.repository2;

import java.util.Optional;

import com.woory.backend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	@Query("select u from User u left join fetch u.groups where u.userId = :userId")
	Optional<User> findByUserId(@Param("userId") long id);
}
