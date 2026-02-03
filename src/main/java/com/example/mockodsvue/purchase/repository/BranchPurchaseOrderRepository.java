package com.example.mockodsvue.purchase.repository;

import com.example.mockodsvue.purchase.model.entity.BranchPurchaseOrder;
import com.example.mockodsvue.delivery.model.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BranchPurchaseOrderRepository extends JpaRepository<BranchPurchaseOrder, Integer> {

    Optional<BranchPurchaseOrder> findByBpoNo(String bpoNo);

    List<BranchPurchaseOrder> findByBranchCode(String branchCode);

    List<BranchPurchaseOrder> findByBranchCodeAndPurchaseDate(String branchCode, LocalDate purchaseDate);

    List<BranchPurchaseOrder> findByBranchCodeAndStatus(String branchCode, DeliveryStatus status);

    List<BranchPurchaseOrder> findByFactoryCode(String factoryCode);
}
