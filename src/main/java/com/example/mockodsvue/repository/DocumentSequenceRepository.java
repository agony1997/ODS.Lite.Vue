package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.sequence.DocumentSequence;
import com.example.mockodsvue.model.entity.sequence.DocumentSequenceId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DocumentSequenceRepository extends JpaRepository<DocumentSequence, DocumentSequenceId> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM DocumentSequence s WHERE s.sequenceType = :type AND s.sequenceDate = :date")
    Optional<DocumentSequence> findByTypeAndDateWithLock(
            @Param("type") String type,
            @Param("date") LocalDate date
    );
}
