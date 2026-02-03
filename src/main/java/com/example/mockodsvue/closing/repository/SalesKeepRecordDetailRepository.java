package com.example.mockodsvue.closing.repository;

import com.example.mockodsvue.closing.model.entity.SalesKeepRecordDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesKeepRecordDetailRepository extends JpaRepository<SalesKeepRecordDetail, Integer> {

    List<SalesKeepRecordDetail> findByKeepNo(String keepNo);

    List<SalesKeepRecordDetail> findByKeepNoOrderByItemNo(String keepNo);

    void deleteByKeepNo(String keepNo);
}
