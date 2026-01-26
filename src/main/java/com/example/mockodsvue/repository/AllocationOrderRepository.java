package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.allocation.AllocationOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AllocationOrderRepository extends JpaRepository<AllocationOrder, Integer> {

    Optional<AllocationOrder> findByAllocationNo(String allocationNo);

    List<AllocationOrder> findByBranchCode(String branchCode);

    List<AllocationOrder> findByBranchCodeAndAllocationDate(String branchCode, LocalDate allocationDate);
}
