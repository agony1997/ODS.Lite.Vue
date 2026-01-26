package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.delivery.SalesDeliveryOrder;
import com.example.mockodsvue.model.enums.DeliveryOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesDeliveryOrderRepository extends JpaRepository<SalesDeliveryOrder, Integer> {

    Optional<SalesDeliveryOrder> findByDeliveryNo(String deliveryNo);

    List<SalesDeliveryOrder> findByBranchCode(String branchCode);

    List<SalesDeliveryOrder> findByBranchCodeAndLocationCode(String branchCode, String locationCode);

    List<SalesDeliveryOrder> findByBranchCodeAndDeliveryDate(String branchCode, LocalDate deliveryDate);

    List<SalesDeliveryOrder> findByBranchCodeAndStatus(String branchCode, DeliveryOrderStatus status);

    List<SalesDeliveryOrder> findByCustomerCode(String customerCode);

    List<SalesDeliveryOrder> findByLocationCode(String locationCode);

    List<SalesDeliveryOrder> findByLocationCodeAndDeliveryDate(String locationCode, LocalDate deliveryDate);
}
