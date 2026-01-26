package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.receive.FactoryDeliveryOrder;
import com.example.mockodsvue.model.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FactoryDeliveryOrderRepository extends JpaRepository<FactoryDeliveryOrder, Integer> {

    Optional<FactoryDeliveryOrder> findByFdoNo(String fdoNo);

    List<FactoryDeliveryOrder> findByBpoNo(String bpoNo);

    List<FactoryDeliveryOrder> findByBranchCode(String branchCode);

    List<FactoryDeliveryOrder> findByBranchCodeAndDeliveryDate(String branchCode, LocalDate deliveryDate);

    List<FactoryDeliveryOrder> findByBranchCodeAndStatus(String branchCode, DeliveryStatus status);

    List<FactoryDeliveryOrder> findByFactoryCode(String factoryCode);
}
