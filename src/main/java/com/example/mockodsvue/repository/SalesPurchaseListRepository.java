package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.purchase.SalesPurchaseList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesPurchaseListRepository extends JpaRepository<SalesPurchaseList, Integer> {

}
