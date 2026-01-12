package com.example.mockodsvue.model.dto;

import lombok.Data;

@Data
public class BranchProductListDTO {
    private String branchCode;
    private String productCode;
    private String productName;
    private String unit;
    private int sortOrder;
}
