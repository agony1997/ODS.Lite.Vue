package com.example.mockodsvue.delivery.repository;

import com.example.mockodsvue.delivery.model.entity.SalesDeliveryOrderDetail;
import com.example.mockodsvue.delivery.model.enums.DeliveryDetailType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesDeliveryOrderDetailRepository extends JpaRepository<SalesDeliveryOrderDetail, Integer> {

    List<SalesDeliveryOrderDetail> findByDeliveryNo(String deliveryNo);

    List<SalesDeliveryOrderDetail> findByDeliveryNoOrderByItemNo(String deliveryNo);

    List<SalesDeliveryOrderDetail> findByDeliveryNoAndType(String deliveryNo, DeliveryDetailType type);

    List<SalesDeliveryOrderDetail> findByPreOrderNo(String preOrderNo);

    void deleteByDeliveryNo(String deliveryNo);
}
