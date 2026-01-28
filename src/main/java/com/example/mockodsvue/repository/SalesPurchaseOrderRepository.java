package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.purchase.SalesPurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesPurchaseOrderRepository extends JpaRepository<SalesPurchaseOrder, Integer> {

    Optional<SalesPurchaseOrder> findByPurchaseNo(String purchaseNo);

    Optional<SalesPurchaseOrder> findByLocationCodeAndPurchaseDate(String locationCode, LocalDate purchaseDate);

    boolean existsByLocationCodeAndPurchaseDate(String locationCode, LocalDate purchaseDate);

    /**
     * 依營業所代碼和訂貨日期查詢所有業務員訂貨單
     */
    List<SalesPurchaseOrder> findByBranchCodeAndPurchaseDate(String branchCode, LocalDate purchaseDate);
}
