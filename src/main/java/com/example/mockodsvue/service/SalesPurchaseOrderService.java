package com.example.mockodsvue.service;

import com.example.mockodsvue.model.dto.SalesPurchaseDTO;
import com.example.mockodsvue.model.entity.purchase.SalesPurchaseOrder;
import com.example.mockodsvue.repository.SalesPurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SalesPurchaseOrderService {

    private final SalesPurchaseOrderRepository salesPurchaseOrderRepository;

    private SalesPurchaseOrder exist(String purchaseNo) {
        return null;
    }

    private SalesPurchaseOrder exist(String location, LocalDate date) {
        return null;
    }

    public SalesPurchaseDTO findByCondition(String location, LocalDate date) {
        return null;
    }

    public SalesPurchaseDTO update(SalesPurchaseDTO dto) {
        return null;
    }

    private SalesPurchaseOrder create() {
        return null;
    }

    public SalesPurchaseOrder createFromYesterdayOrder() {
        return null;
    }

}
