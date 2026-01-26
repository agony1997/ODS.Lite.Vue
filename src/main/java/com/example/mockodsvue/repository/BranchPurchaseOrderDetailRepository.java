package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.purchase.BranchPurchaseOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchPurchaseOrderDetailRepository extends JpaRepository<BranchPurchaseOrderDetail, Integer> {

    List<BranchPurchaseOrderDetail> findByBpoNo(String bpoNo);

    List<BranchPurchaseOrderDetail> findByBpoNoOrderByItemNo(String bpoNo);

    void deleteByBpoNo(String bpoNo);
}
