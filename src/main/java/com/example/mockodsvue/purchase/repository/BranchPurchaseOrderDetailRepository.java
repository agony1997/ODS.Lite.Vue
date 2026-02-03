package com.example.mockodsvue.purchase.repository;

import com.example.mockodsvue.purchase.model.entity.BranchPurchaseOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchPurchaseOrderDetailRepository extends JpaRepository<BranchPurchaseOrderDetail, Integer> {

    List<BranchPurchaseOrderDetail> findByBpoNo(String bpoNo);

    List<BranchPurchaseOrderDetail> findByBpoNoOrderByItemNo(String bpoNo);

    void deleteByBpoNo(String bpoNo);
}
