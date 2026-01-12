package com.example.mockodsvue.model.dto;

import lombok.Data;

@Data
public class SalesPurchaseListDTO {
    private String productCode;
    private String unit;
    private int qty;
    private int sortOrder;
}
