package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.purchase.SalesPurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesPurchaseOrderRepository extends JpaRepository<SalesPurchaseOrder, Integer> {

}
