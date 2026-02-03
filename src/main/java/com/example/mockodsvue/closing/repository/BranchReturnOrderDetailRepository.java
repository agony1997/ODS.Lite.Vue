package com.example.mockodsvue.closing.repository;

import com.example.mockodsvue.closing.model.entity.BranchReturnOrderDetail;
import com.example.mockodsvue.closing.model.enums.ReturnReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchReturnOrderDetailRepository extends JpaRepository<BranchReturnOrderDetail, Integer> {

    List<BranchReturnOrderDetail> findByBroNo(String broNo);

    List<BranchReturnOrderDetail> findByBroNoOrderByItemNo(String broNo);

    List<BranchReturnOrderDetail> findByBroNoAndReason(String broNo, ReturnReason reason);

    List<BranchReturnOrderDetail> findByReturnNo(String returnNo);

    void deleteByBroNo(String broNo);
}
