package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.purchase.SalesPurchaseOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesPurchaseOrderDetailRepository extends JpaRepository<SalesPurchaseOrderDetail, Integer> {

}
