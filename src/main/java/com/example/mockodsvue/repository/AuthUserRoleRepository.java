package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.auth.AuthUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthUserRoleRepository extends JpaRepository<AuthUserRole, Integer> {

}
