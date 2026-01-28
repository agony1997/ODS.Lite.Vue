package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.purchase.BranchPurchaseFrozen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface BranchPurchaseFrozenRepository extends JpaRepository<BranchPurchaseFrozen, Integer> {

    /**
     * 依營業所代碼和訂貨日期查詢凍結記錄
     */
    Optional<BranchPurchaseFrozen> findByBranchCodeAndPurchaseDate(String branchCode, LocalDate purchaseDate);

    /**
     * 刪除營業所凍結記錄
     */
    void deleteByBranchCodeAndPurchaseDate(String branchCode, LocalDate purchaseDate);
}
