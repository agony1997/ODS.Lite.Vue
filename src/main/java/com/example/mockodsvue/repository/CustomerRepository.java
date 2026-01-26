package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.master.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    List<Customer> findByBranchCode(String branchCode);

    List<Customer> findByStatus(String status);

    List<Customer> findByBranchCodeAndStatus(String branchCode, String status);
}
