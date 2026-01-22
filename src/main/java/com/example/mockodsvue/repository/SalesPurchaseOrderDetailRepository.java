package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.purchase.SalesPurchaseOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesPurchaseOrderDetailRepository extends JpaRepository<SalesPurchaseOrderDetail, Integer> {

    List<SalesPurchaseOrderDetail> findByPurchaseNoOrderByItemNo(String purchaseNo);

    void deleteByPurchaseNo(String purchaseNo);
}
