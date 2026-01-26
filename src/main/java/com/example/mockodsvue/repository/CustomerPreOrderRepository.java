package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.delivery.CustomerPreOrder;
import com.example.mockodsvue.model.enums.PreOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerPreOrderRepository extends JpaRepository<CustomerPreOrder, Integer> {

    Optional<CustomerPreOrder> findByPreOrderNo(String preOrderNo);

    List<CustomerPreOrder> findByBranchCode(String branchCode);

    List<CustomerPreOrder> findByBranchCodeAndLocationCode(String branchCode, String locationCode);

    List<CustomerPreOrder> findByBranchCodeAndDeliveryDate(String branchCode, LocalDate deliveryDate);

    List<CustomerPreOrder> findByBranchCodeAndStatus(String branchCode, PreOrderStatus status);

    List<CustomerPreOrder> findByCustomerCode(String customerCode);

    List<CustomerPreOrder> findByLocationCode(String locationCode);

    List<CustomerPreOrder> findByLocationCodeAndDeliveryDate(String locationCode, LocalDate deliveryDate);
}
