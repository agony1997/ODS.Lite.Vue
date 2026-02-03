package com.example.mockodsvue.sequence.service.impl;

import com.example.mockodsvue.shared.exception.BusinessException;
import com.example.mockodsvue.sequence.model.entity.DocumentSequence;
import com.example.mockodsvue.sequence.model.enums.SequenceType;
import com.example.mockodsvue.sequence.repository.DocumentSequenceRepository;
import com.example.mockodsvue.sequence.service.SequenceGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SequenceGeneratorImpl implements SequenceGenerator {

    private final DocumentSequenceRepository sequenceRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int MAX_SEQUENCE_NO = 999;
    private static final int MAX_RETRY = 3;

    /**
     * 產生單據編號（併發安全）
     * 使用獨立事務確保序號不會因外層事務回滾而丟失
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generate(SequenceType type, LocalDate date) {
        int retryCount = 0;

        while (retryCount < MAX_RETRY) {
            try {
                return doGenerate(type, date);
            } catch (Exception e) {
                retryCount++;
                log.warn("取號失敗，重試第 {} 次: {}", retryCount, e.getMessage());
                if (retryCount >= MAX_RETRY) {
                    throw new BusinessException("取號失敗，請稍後再試");
                }
            }
        }

        throw new BusinessException("取號失敗，請稍後再試");
    }

    private String doGenerate(SequenceType type, LocalDate date) {
        DocumentSequence sequence = sequenceRepository
                .findByTypeAndDateWithLock(type.getCode(), date)
                .orElseGet(() -> createNewSequence(type, date));

        int nextNo = sequence.getCurrentNo() + 1;

        if (nextNo > MAX_SEQUENCE_NO) {
            throw new BusinessException(
                    String.format("單據類型 %s 於 %s 的序號已達上限 (%d)",
                            type.getName(), date, MAX_SEQUENCE_NO)
            );
        }

        sequence.setCurrentNo(nextNo);
        sequenceRepository.save(sequence);

        String dateStr = date.format(DATE_FORMAT);
        return String.format("%s-%s-%03d", type.getCode(), dateStr, nextNo);
    }

    private DocumentSequence createNewSequence(SequenceType type, LocalDate date) {
        DocumentSequence sequence = new DocumentSequence();
        sequence.setSequenceType(type.getCode());
        sequence.setSequenceDate(date);
        sequence.setCurrentNo(0);
        return sequenceRepository.save(sequence);
    }
}
