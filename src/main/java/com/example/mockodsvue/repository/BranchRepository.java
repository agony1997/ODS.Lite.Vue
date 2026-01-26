package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.branch.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {

    Optional<Branch> findByBranchCode(String branchCode);

    List<Branch> findByStatus(String status);
}
