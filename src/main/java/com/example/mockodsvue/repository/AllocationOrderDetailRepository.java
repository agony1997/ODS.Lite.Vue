package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.allocation.AllocationOrderDetail;
import com.example.mockodsvue.model.enums.AllocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllocationOrderDetailRepository extends JpaRepository<AllocationOrderDetail, Integer> {

    List<AllocationOrderDetail> findByAllocationNo(String allocationNo);

    List<AllocationOrderDetail> findByAllocationNoOrderByItemNo(String allocationNo);

    List<AllocationOrderDetail> findByAllocationNoAndStatus(String allocationNo, AllocationStatus status);

    List<AllocationOrderDetail> findByLocationCode(String locationCode);

    List<AllocationOrderDetail> findByLocationCodeAndStatus(String locationCode, AllocationStatus status);

    void deleteByAllocationNo(String allocationNo);
}
