package com.example.mockodsvue.allocation.repository;

import com.example.mockodsvue.allocation.model.entity.SalesReceiveOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesReceiveOrderDetailRepository extends JpaRepository<SalesReceiveOrderDetail, Integer> {

    List<SalesReceiveOrderDetail> findByReceiveNo(String receiveNo);

    List<SalesReceiveOrderDetail> findByReceiveNoOrderByItemNo(String receiveNo);

    List<SalesReceiveOrderDetail> findByAllocationNo(String allocationNo);

    void deleteByReceiveNo(String receiveNo);
}
