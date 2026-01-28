package com.example.mockodsvue.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 彙總請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregateRequestDTO {

    private String branchCode;
    private LocalDate purchaseDate;
}
