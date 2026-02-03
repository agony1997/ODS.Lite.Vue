package com.example.mockodsvue.allocation.repository;

import com.example.mockodsvue.allocation.model.entity.SalesReceiveOrder;
import com.example.mockodsvue.allocation.model.enums.AllocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesReceiveOrderRepository extends JpaRepository<SalesReceiveOrder, Integer> {

    Optional<SalesReceiveOrder> findByReceiveNo(String receiveNo);

    List<SalesReceiveOrder> findByBranchCode(String branchCode);

    List<SalesReceiveOrder> findByBranchCodeAndLocationCode(String branchCode, String locationCode);

    List<SalesReceiveOrder> findByBranchCodeAndReceiveDate(String branchCode, LocalDate receiveDate);

    List<SalesReceiveOrder> findByBranchCodeAndStatus(String branchCode, AllocationStatus status);

    List<SalesReceiveOrder> findByLocationCode(String locationCode);
}
