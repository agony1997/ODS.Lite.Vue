package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.branch.BranchProductList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchProductListRepository extends JpaRepository<BranchProductList, Integer> {

}
