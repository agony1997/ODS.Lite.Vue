package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.auth.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Integer> {

    Optional<AuthUser> findByUserId(String userId);

}
