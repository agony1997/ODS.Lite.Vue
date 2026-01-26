package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.auth.AuthUserBranchRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthUserBranchRoleRepository extends JpaRepository<AuthUserBranchRole, Integer> {

    List<AuthUserBranchRole> findByUserId(String userId);

    List<AuthUserBranchRole> findByUserIdAndBranchCode(String userId, String branchCode);

    Optional<AuthUserBranchRole> findByUserIdAndBranchCodeAndRoleCode(String userId, String branchCode, String roleCode);

    void deleteByUserId(String userId);

    void deleteByRoleCode(String roleCode);

}
