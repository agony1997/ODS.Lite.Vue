package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.closing.SalesReturnRecord;
import com.example.mockodsvue.model.enums.ReturnProcessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesReturnRecordRepository extends JpaRepository<SalesReturnRecord, Integer> {

    Optional<SalesReturnRecord> findByReturnNo(String returnNo);

    List<SalesReturnRecord> findByBranchCode(String branchCode);

    List<SalesReturnRecord> findByBranchCodeAndLocationCode(String branchCode, String locationCode);

    List<SalesReturnRecord> findByBranchCodeAndReturnDate(String branchCode, LocalDate returnDate);

    List<SalesReturnRecord> findByBranchCodeAndStatus(String branchCode, ReturnProcessStatus status);

    List<SalesReturnRecord> findByLocationCode(String locationCode);

    List<SalesReturnRecord> findByLocationCodeAndStatus(String locationCode, ReturnProcessStatus status);
}
