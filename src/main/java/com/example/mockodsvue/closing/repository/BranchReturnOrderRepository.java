package com.example.mockodsvue.closing.repository;

import com.example.mockodsvue.closing.model.entity.BranchReturnOrder;
import com.example.mockodsvue.closing.model.enums.BranchReturnStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BranchReturnOrderRepository extends JpaRepository<BranchReturnOrder, Integer> {

    Optional<BranchReturnOrder> findByBroNo(String broNo);

    List<BranchReturnOrder> findByBranchCode(String branchCode);

    List<BranchReturnOrder> findByBranchCodeAndReturnDate(String branchCode, LocalDate returnDate);

    List<BranchReturnOrder> findByBranchCodeAndStatus(String branchCode, BranchReturnStatus status);

    List<BranchReturnOrder> findByFactoryCode(String factoryCode);

    List<BranchReturnOrder> findByFactoryCodeAndStatus(String factoryCode, BranchReturnStatus status);
}
