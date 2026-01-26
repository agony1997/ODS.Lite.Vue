package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.closing.SalesReturnRecordDetail;
import com.example.mockodsvue.model.enums.ReturnReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesReturnRecordDetailRepository extends JpaRepository<SalesReturnRecordDetail, Integer> {

    List<SalesReturnRecordDetail> findByReturnNo(String returnNo);

    List<SalesReturnRecordDetail> findByReturnNoOrderByItemNo(String returnNo);

    List<SalesReturnRecordDetail> findByReturnNoAndReason(String returnNo, ReturnReason reason);

    void deleteByReturnNo(String returnNo);
}
