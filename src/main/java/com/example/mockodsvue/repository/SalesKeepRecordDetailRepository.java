package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.closing.SalesKeepRecordDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesKeepRecordDetailRepository extends JpaRepository<SalesKeepRecordDetail, Integer> {

    List<SalesKeepRecordDetail> findByKeepNo(String keepNo);

    List<SalesKeepRecordDetail> findByKeepNoOrderByItemNo(String keepNo);

    void deleteByKeepNo(String keepNo);
}
