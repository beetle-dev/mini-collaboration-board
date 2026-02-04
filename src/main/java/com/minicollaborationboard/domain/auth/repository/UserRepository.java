package com.minicollaborationboard.domain.auth.repository;

import com.minicollaborationboard.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    User findByUuid(String uuid);

    void deleteAllByEmail(String testEmail);
}
