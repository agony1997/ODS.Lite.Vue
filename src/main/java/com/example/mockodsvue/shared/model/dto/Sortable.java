package com.example.mockodsvue.shared.model.dto;

/**
 * 標記可排序的 DTO
 * 實作此介面的 DTO 表示支援排序功能，前端傳入的 List 順序即為排序順序
 */
public interface Sortable {
    int getSortOrder();
    void setSortOrder(int sortOrder);
}
