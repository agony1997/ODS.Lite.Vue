package com.example.mockodsvue.auth.repository;

import com.example.mockodsvue.auth.model.entity.AuthUserBranchRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthUserBranchRoleRepository extends JpaRepository<AuthUserBranchRole, Integer> {

    List<AuthUserBranchRole> findByUserCode(String userCode);

    List<AuthUserBranchRole> findByUserCodeAndBranchCode(String userCode, String branchCode);

    Optional<AuthUserBranchRole> findByUserCodeAndBranchCodeAndRoleCode(String userCode, String branchCode, String roleCode);

    void deleteByUserCode(String userCode);

    void deleteByRoleCode(String roleCode);

}
