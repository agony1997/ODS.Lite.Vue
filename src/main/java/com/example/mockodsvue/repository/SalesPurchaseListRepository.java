package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.purchase.SalesPurchaseList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesPurchaseListRepository extends JpaRepository<SalesPurchaseList, Integer> {

    List<SalesPurchaseList> findByLocationCodeOrderBySortOrder(String locationCode);

    void deleteByLocationCode(String locationCode);
}
