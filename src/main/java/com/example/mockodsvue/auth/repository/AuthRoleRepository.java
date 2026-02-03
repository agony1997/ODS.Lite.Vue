package com.example.mockodsvue.auth.repository;

import com.example.mockodsvue.auth.model.entity.AuthRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRoleRepository extends JpaRepository<AuthRole, Integer> {

    Optional<AuthRole> findByRoleCode(String roleCode);

}
