package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.purchase.SalesPurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SalesPurchaseOrderRepository extends JpaRepository<SalesPurchaseOrder, Integer> {

    Optional<SalesPurchaseOrder> findByPurchaseNo(String purchaseNo);

    Optional<SalesPurchaseOrder> findByLocationCodeAndPurchaseDate(String locationCode, LocalDate purchaseDate);

    boolean existsByLocationCodeAndPurchaseDate(String locationCode, LocalDate purchaseDate);
}
