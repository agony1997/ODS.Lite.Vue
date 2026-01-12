package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.purchase.BranchPurchaseFrozen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchPurchaseFrozenRepository extends JpaRepository<BranchPurchaseFrozen, Integer> {

}
