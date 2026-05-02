package com.API.BlogV2.Repository;

import com.API.BlogV2.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findById(Long userId);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
