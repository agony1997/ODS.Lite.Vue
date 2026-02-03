package com.example.mockodsvue.closing.repository;

import com.example.mockodsvue.closing.model.entity.SalesKeepRecord;
import com.example.mockodsvue.closing.model.enums.KeepStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesKeepRecordRepository extends JpaRepository<SalesKeepRecord, Integer> {

    Optional<SalesKeepRecord> findByKeepNo(String keepNo);

    List<SalesKeepRecord> findByBranchCode(String branchCode);

    List<SalesKeepRecord> findByBranchCodeAndLocationCode(String branchCode, String locationCode);

    List<SalesKeepRecord> findByBranchCodeAndKeepDate(String branchCode, LocalDate keepDate);

    List<SalesKeepRecord> findByBranchCodeAndStatus(String branchCode, KeepStatus status);

    List<SalesKeepRecord> findByLocationCode(String locationCode);

    List<SalesKeepRecord> findByLocationCodeAndStatus(String locationCode, KeepStatus status);
}
