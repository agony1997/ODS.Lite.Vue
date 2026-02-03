package com.example.mockodsvue.purchase.model.dto;

import com.example.mockodsvue.shared.model.dto.Sortable;
import lombok.Data;

@Data
public class SalesPurchaseListDTO implements Sortable {
    private String productCode;
    private String unit;
    private int qty;
    private int sortOrder;
}
