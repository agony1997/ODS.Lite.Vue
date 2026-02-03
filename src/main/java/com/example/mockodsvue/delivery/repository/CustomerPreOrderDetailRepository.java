package com.example.mockodsvue.delivery.repository;

import com.example.mockodsvue.delivery.model.entity.CustomerPreOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerPreOrderDetailRepository extends JpaRepository<CustomerPreOrderDetail, Integer> {

    List<CustomerPreOrderDetail> findByPreOrderNo(String preOrderNo);

    List<CustomerPreOrderDetail> findByPreOrderNoOrderByItemNo(String preOrderNo);

    void deleteByPreOrderNo(String preOrderNo);
}
