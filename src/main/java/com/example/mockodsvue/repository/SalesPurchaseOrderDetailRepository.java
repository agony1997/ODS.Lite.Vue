package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.purchase.SalesPurchaseOrderDetail;
import com.example.mockodsvue.model.enums.SalesOrderDetailStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SalesPurchaseOrderDetailRepository extends JpaRepository<SalesPurchaseOrderDetail, Integer> {

    List<SalesPurchaseOrderDetail> findByPurchaseNoOrderByItemNo(String purchaseNo);

    void deleteByPurchaseNo(String purchaseNo);

    /**
     * 依多個訂貨單號查詢明細
     */
    List<SalesPurchaseOrderDetail> findByPurchaseNoIn(Collection<String> purchaseNos);

    /**
     * 依多個訂貨單號和狀態查詢明細
     */
    List<SalesPurchaseOrderDetail> findByPurchaseNoInAndStatus(Collection<String> purchaseNos, SalesOrderDetailStatus status);
}
