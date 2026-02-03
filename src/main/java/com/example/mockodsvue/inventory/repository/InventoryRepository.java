package com.example.mockodsvue.inventory.repository;

import com.example.mockodsvue.inventory.model.entity.Inventory;
import com.example.mockodsvue.branch.model.enums.LocationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    List<Inventory> findByBranchCode(String branchCode);

    List<Inventory> findByBranchCodeAndLocationCode(String branchCode, String locationCode);

    List<Inventory> findByBranchCodeAndLocationType(String branchCode, LocationType locationType);

    List<Inventory> findByBranchCodeAndProductCode(String branchCode, String productCode);

    Optional<Inventory> findByBranchCodeAndLocationCodeAndProductCodeAndBatchNo(
            String branchCode, String locationCode, String productCode, String batchNo);
}
