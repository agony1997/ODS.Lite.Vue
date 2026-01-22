package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.branch.BranchProductList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchProductListRepository extends JpaRepository<BranchProductList, Integer> {

    List<BranchProductList> findByBranchCodeOrderBySortOrder(String branchCode);

    Optional<BranchProductList> findByBranchCodeAndProductCodeAndUnit(String branchCode, String productCode, String unit);
}
