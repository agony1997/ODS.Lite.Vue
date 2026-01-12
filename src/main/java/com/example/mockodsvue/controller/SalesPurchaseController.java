package com.example.mockodsvue.controller;

import com.example.mockodsvue.model.dto.SalesPurchaseDTO;
import com.example.mockodsvue.model.dto.SalesPurchaseListDTO;
import com.example.mockodsvue.service.BranchProductListService;
import com.example.mockodsvue.service.SalesPurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/purchase/sales")
public class SalesPurchaseController {

    private final SalesPurchaseOrderService salesPurchaseOrderService;
    private final BranchProductListService branchProductListService;

    @GetMapping("")
    public ResponseEntity<List<SalesPurchaseDTO>> getAll() {
        return null;
    }

    @GetMapping("/sales")
    public ResponseEntity<SalesPurchaseDTO> getOrderByCondition(@RequestParam String locationCode,
                                                                @RequestParam LocalDate date) {
        return null;
    }

    @PatchMapping("")
    public RequestEntity<SalesPurchaseDTO> updateOrder(@RequestBody SalesPurchaseDTO dto) {
        return null;
    }

    @PostMapping("/yesterday")
    public RequestEntity<SalesPurchaseDTO> createByYesterdayOrder(@RequestParam String locationCode) {
        return null;
    }

    @PutMapping("/custom")
    public RequestEntity<List<SalesPurchaseListDTO>> updateCustomList(@RequestBody List<SalesPurchaseListDTO> list) {
        return null;
    }

    @GetMapping("/branch")
    public RequestEntity<List<SalesPurchaseListDTO>> getBranchList() {
        return null;
    }

}