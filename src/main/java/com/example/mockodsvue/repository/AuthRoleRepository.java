package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.auth.AuthRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRoleRepository extends JpaRepository<AuthRole, Integer> {

}
