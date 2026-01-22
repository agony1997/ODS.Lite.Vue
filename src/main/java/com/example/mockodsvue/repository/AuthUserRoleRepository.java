package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.auth.AuthUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthUserRoleRepository extends JpaRepository<AuthUserRole, Integer> {

    List<AuthUserRole> findByEmpNo(String empNo);

    Optional<AuthUserRole> findByEmpNoAndRoleCode(String empNo, String roleCode);

    void deleteByEmpNo(String empNo);

    void deleteByRoleCode(String roleCode);

}
