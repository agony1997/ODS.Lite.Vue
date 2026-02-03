package com.example.mockodsvue.branch.model.dto;

import com.example.mockodsvue.shared.model.dto.Sortable;
import lombok.Data;

@Data
public class BranchProductListDTO implements Sortable {
    private String branchCode;
    private String productCode;
    private String productName;
    private String unit;
    private int sortOrder;
}
