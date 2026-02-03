package com.example.mockodsvue.receive.repository;

import com.example.mockodsvue.receive.model.entity.FactoryDeliveryOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactoryDeliveryOrderDetailRepository extends JpaRepository<FactoryDeliveryOrderDetail, Integer> {

    List<FactoryDeliveryOrderDetail> findByFdoNo(String fdoNo);

    List<FactoryDeliveryOrderDetail> findByFdoNoOrderByItemNo(String fdoNo);

    void deleteByFdoNo(String fdoNo);
}
