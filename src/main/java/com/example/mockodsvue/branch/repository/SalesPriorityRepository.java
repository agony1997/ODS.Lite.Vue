package com.example.mockodsvue.branch.repository;

import com.example.mockodsvue.branch.model.entity.SalesPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalesPriorityRepository extends JpaRepository<SalesPriority, Integer> {

    List<SalesPriority> findByBranchCodeOrderByPriorityLevel(String branchCode);

    Optional<SalesPriority> findByBranchCodeAndLocationCode(String branchCode, String locationCode);
}
