package com.example.mockodsvue.sequence.service;

import com.example.mockodsvue.sequence.model.enums.SequenceType;

import java.time.LocalDate;

public interface SequenceGenerator {

    /**
     * 產生單據編號
     *
     * @param type 單據類型
     * @param date 單據日期
     * @return 完整單據編號
     */
    String generate(SequenceType type, LocalDate date);
}
