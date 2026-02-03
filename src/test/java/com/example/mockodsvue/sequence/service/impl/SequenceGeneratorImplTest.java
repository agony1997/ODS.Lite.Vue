package com.example.mockodsvue.sequence.service.impl;

import com.example.mockodsvue.shared.exception.BusinessException;
import com.example.mockodsvue.sequence.model.entity.DocumentSequence;
import com.example.mockodsvue.sequence.model.enums.SequenceType;
import com.example.mockodsvue.sequence.repository.DocumentSequenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SequenceGeneratorImpl 測試")
class SequenceGeneratorImplTest {

    @Mock
    private DocumentSequenceRepository sequenceRepository;

    @InjectMocks
    private SequenceGeneratorImpl sequenceGenerator;

    private LocalDate testDate;
    private DocumentSequence existingSequence;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2026, 1, 25);

        existingSequence = new DocumentSequence();
        existingSequence.setSequenceType(SequenceType.SPO.getCode());
        existingSequence.setSequenceDate(testDate);
        existingSequence.setCurrentNo(5);
        existingSequence.setVersion(1L);
    }

    // ==================== 基本取號測試 ====================

    @Nested
    @DisplayName("基本取號測試")
    class BasicGenerateTest {

        @Test
        @DisplayName("產生第一個序號 - 新建序號記錄")
        void generateFirstSequence_Success() {
            // given
            when(sequenceRepository.findByTypeAndDateWithLock(SequenceType.SPO.getCode(), testDate))
                    .thenReturn(Optional.empty());
            when(sequenceRepository.save(any(DocumentSequence.class)))
                    .thenAnswer(invocation -> {
                        DocumentSequence seq = invocation.getArgument(0);
                        seq.setVersion(1L);
                        return seq;
                    });

            // when
            String result = sequenceGenerator.generate(SequenceType.SPO, testDate);

            // then
            assertEquals("SPO-20260125-001", result);
            verify(sequenceRepository, times(2)).save(any(DocumentSequence.class));
        }

        @Test
        @DisplayName("產生下一個序號 - 遞增現有序號")
        void generateNextSequence_Success() {
            // given
            when(sequenceRepository.findByTypeAndDateWithLock(SequenceType.SPO.getCode(), testDate))
                    .thenReturn(Optional.of(existingSequence));
            when(sequenceRepository.save(any(DocumentSequence.class)))
                    .thenReturn(existingSequence);

            // when
            String result = sequenceGenerator.generate(SequenceType.SPO, testDate);

            // then
            assertEquals("SPO-20260125-006", result);
            assertEquals(6, existingSequence.getCurrentNo());
            verify(sequenceRepository).save(existingSequence);
        }

        @Test
        @DisplayName("不同單據類型產生不同序號")
        void generateDifferentTypes_Success() {
            // given
            DocumentSequence bpoSequence = new DocumentSequence();
            bpoSequence.setSequenceType(SequenceType.BPO.getCode());
            bpoSequence.setSequenceDate(testDate);
            bpoSequence.setCurrentNo(10);

            when(sequenceRepository.findByTypeAndDateWithLock(SequenceType.BPO.getCode(), testDate))
                    .thenReturn(Optional.of(bpoSequence));
            when(sequenceRepository.save(any(DocumentSequence.class)))
                    .thenReturn(bpoSequence);

            // when
            String result = sequenceGenerator.generate(SequenceType.BPO, testDate);

            // then
            assertEquals("BPO-20260125-011", result);
        }

        @Test
        @DisplayName("不同日期產生不同序號")
        void generateDifferentDates_Success() {
            // given
            LocalDate anotherDate = LocalDate.of(2026, 1, 26);
            when(sequenceRepository.findByTypeAndDateWithLock(SequenceType.SPO.getCode(), anotherDate))
                    .thenReturn(Optional.empty());
            when(sequenceRepository.save(any(DocumentSequence.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            String result = sequenceGenerator.generate(SequenceType.SPO, anotherDate);

            // then
            assertEquals("SPO-20260126-001", result);
        }
    }

    // ==================== 序號格式測試 ====================

    @Nested
    @DisplayName("序號格式測試")
    class SequenceFormatTest {

        @Test
        @DisplayName("序號格式正確 - 單位數")
        void sequenceFormat_SingleDigit() {
            // given
            existingSequence.setCurrentNo(0);
            when(sequenceRepository.findByTypeAndDateWithLock(SequenceType.SPO.getCode(), testDate))
                    .thenReturn(Optional.of(existingSequence));
            when(sequenceRepository.save(any())).thenReturn(existingSequence);

            // when
            String result = sequenceGenerator.generate(SequenceType.SPO, testDate);

            // then
            assertEquals("SPO-20260125-001", result);
        }

        @Test
        @DisplayName("序號格式正確 - 兩位數")
        void sequenceFormat_TwoDigits() {
            // given
            existingSequence.setCurrentNo(9);
            when(sequenceRepository.findByTypeAndDateWithLock(SequenceType.SPO.getCode(), testDate))
                    .thenReturn(Optional.of(existingSequence));
            when(sequenceRepository.save(any())).thenReturn(existingSequence);

            // when
            String result = sequenceGenerator.generate(SequenceType.SPO, testDate);

            // then
            assertEquals("SPO-20260125-010", result);
        }

        @Test
        @DisplayName("序號格式正確 - 三位數")
        void sequenceFormat_ThreeDigits() {
            // given
            existingSequence.setCurrentNo(99);
            when(sequenceRepository.findByTypeAndDateWithLock(SequenceType.SPO.getCode(), testDate))
                    .thenReturn(Optional.of(existingSequence));
            when(sequenceRepository.save(any())).thenReturn(existingSequence);

            // when
            String result = sequenceGenerator.generate(SequenceType.SPO, testDate);

            // then
            assertEquals("SPO-20260125-100", result);
        }

        @Test
        @DisplayName("所有單據類型格式正確")
        void allSequenceTypes_CorrectFormat() {
            // given
            for (SequenceType type : SequenceType.values()) {
                DocumentSequence seq = new DocumentSequence();
                seq.setSequenceType(type.getCode());
                seq.setSequenceDate(testDate);
                seq.setCurrentNo(0);

                when(sequenceRepository.findByTypeAndDateWithLock(type.getCode(), testDate))
                        .thenReturn(Optional.of(seq));
                when(sequenceRepository.save(any())).thenReturn(seq);

                // when
                String result = sequenceGenerator.generate(type, testDate);

                // then
                assertTrue(result.startsWith(type.getCode() + "-"));
                assertTrue(result.matches("^[A-Z]{2,3}-\\d{8}-\\d{3}$"),
                        "序號格式應為 XX(X)-YYYYMMDD-NNN，實際為: " + result);
            }
        }
    }

    // ==================== 邊界條件測試 ====================

    @Nested
    @DisplayName("邊界條件測試")
    class BoundaryTest {

        @Test
        @DisplayName("序號達到上限 - 拋出例外")
        void sequenceReachesLimit_ThrowsException() {
            // given
            existingSequence.setCurrentNo(999);
            when(sequenceRepository.findByTypeAndDateWithLock(SequenceType.SPO.getCode(), testDate))
                    .thenReturn(Optional.of(existingSequence));

            // when & then
            // 注意：因為重試機制會捕獲原始例外，最終拋出通用錯誤訊息
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> sequenceGenerator.generate(SequenceType.SPO, testDate)
            );

            assertEquals("取號失敗，請稍後再試", exception.getMessage());
            // 驗證重試了 3 次
            verify(sequenceRepository, times(3)).findByTypeAndDateWithLock(SequenceType.SPO.getCode(), testDate);
        }

        @Test
        @DisplayName("序號接近上限 - 仍可產生")
        void sequenceNearLimit_Success() {
            // given
            existingSequence.setCurrentNo(998);
            when(sequenceRepository.findByTypeAndDateWithLock(SequenceType.SPO.getCode(), testDate))
                    .thenReturn(Optional.of(existingSequence));
            when(sequenceRepository.save(any())).thenReturn(existingSequence);

            // when
            String result = sequenceGenerator.generate(SequenceType.SPO, testDate);

            // then
            assertEquals("SPO-20260125-999", result);
        }
    }

    // ==================== 重試機制測試 ====================

    @Nested
    @DisplayName("重試機制測試")
    class RetryTest {

        @Test
        @DisplayName("第一次失敗後重試成功")
        void retryAfterFirstFailure_Success() {
            // given
            when(sequenceRepository.findByTypeAndDateWithLock(SequenceType.SPO.getCode(), testDate))
                    .thenThrow(new RuntimeException("Lock failed"))
                    .thenReturn(Optional.of(existingSequence));
            when(sequenceRepository.save(any())).thenReturn(existingSequence);

            // when
            String result = sequenceGenerator.generate(SequenceType.SPO, testDate);

            // then
            assertEquals("SPO-20260125-006", result);
            verify(sequenceRepository, times(2)).findByTypeAndDateWithLock(SequenceType.SPO.getCode(), testDate);
        }

        @Test
        @DisplayName("連續失敗超過重試次數 - 拋出例外")
        void exceedMaxRetry_ThrowsException() {
            // given
            when(sequenceRepository.findByTypeAndDateWithLock(SequenceType.SPO.getCode(), testDate))
                    .thenThrow(new RuntimeException("Lock failed"));

            // when & then
            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> sequenceGenerator.generate(SequenceType.SPO, testDate)
            );

            assertEquals("取號失敗，請稍後再試", exception.getMessage());
            verify(sequenceRepository, times(3)).findByTypeAndDateWithLock(SequenceType.SPO.getCode(), testDate);
        }

        @Test
        @DisplayName("第三次重試成功")
        void thirdRetrySuccess() {
            // given
            when(sequenceRepository.findByTypeAndDateWithLock(SequenceType.SPO.getCode(), testDate))
                    .thenThrow(new RuntimeException("Lock failed"))
                    .thenThrow(new RuntimeException("Lock failed again"))
                    .thenReturn(Optional.of(existingSequence));
            when(sequenceRepository.save(any())).thenReturn(existingSequence);

            // when
            String result = sequenceGenerator.generate(SequenceType.SPO, testDate);

            // then
            assertEquals("SPO-20260125-006", result);
            verify(sequenceRepository, times(3)).findByTypeAndDateWithLock(SequenceType.SPO.getCode(), testDate);
        }
    }

    // ==================== 併發安全測試 (單元測試層級) ====================

    @Nested
    @DisplayName("併發相關測試")
    class ConcurrencyTest {

        @Test
        @DisplayName("樂觀鎖版本號應被保留")
        void versionShouldBePreserved() {
            // given
            existingSequence.setVersion(5L);
            when(sequenceRepository.findByTypeAndDateWithLock(SequenceType.SPO.getCode(), testDate))
                    .thenReturn(Optional.of(existingSequence));
            when(sequenceRepository.save(any())).thenReturn(existingSequence);

            // when
            sequenceGenerator.generate(SequenceType.SPO, testDate);

            // then
            verify(sequenceRepository).save(argThat(seq ->
                    seq.getVersion() != null && seq.getVersion() == 5L
            ));
        }

        @Test
        @DisplayName("新建序號記錄並遞增")
        void newSequenceInitialValues() {
            // given
            when(sequenceRepository.findByTypeAndDateWithLock(SequenceType.BPO.getCode(), testDate))
                    .thenReturn(Optional.empty());
            when(sequenceRepository.save(any(DocumentSequence.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // when
            String result = sequenceGenerator.generate(SequenceType.BPO, testDate);

            // then
            // 驗證結果為第一個序號
            assertEquals("BPO-20260125-001", result);
            // 驗證 save 被呼叫兩次 (createNewSequence + doGenerate)
            verify(sequenceRepository, times(2)).save(any(DocumentSequence.class));
        }
    }
}
