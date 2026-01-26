package com.example.mockodsvue.model.dto;

/**
 * 可排序且具有批次的 DTO
 * 繼承 Sortable，額外提供批次號欄位
 * 排序規則：先按 sortOrder ASC，再按 batchNo DESC
 */
public interface BatchSortable extends Sortable {
    String getBatchNo();
}
