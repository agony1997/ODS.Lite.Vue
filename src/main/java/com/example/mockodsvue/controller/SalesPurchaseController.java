package com.example.mockodsvue.controller;

import com.example.mockodsvue.model.dto.SalesPurchaseDTO;
import com.example.mockodsvue.model.dto.SalesPurchaseListDTO;
import com.example.mockodsvue.service.SalesPurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/purchase/sales")
public class SalesPurchaseController {

    private final SalesPurchaseOrderService salesPurchaseOrderService;

    /**
     * 查詢訂單 (若不存在則自動建立)
     * GET /api/purchase/sales?locationCode=xxx&date=yyyy-MM-dd
     */
    @GetMapping("")
    public ResponseEntity<SalesPurchaseDTO> getOrder(
            @RequestParam String locationCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();
        SalesPurchaseDTO result = salesPurchaseOrderService.findOrCreateByCondition(locationCode, date, currentUser);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新訂單
     * PUT /api/purchase/sales
     */
    @PutMapping("")
    public ResponseEntity<SalesPurchaseDTO> updateOrder(@RequestBody SalesPurchaseDTO dto) {
        SalesPurchaseDTO result = salesPurchaseOrderService.updateOrder(dto);
        return ResponseEntity.ok(result);
    }

    /**
     * 帶入上次訂單的產品清單與數量
     * POST /api/purchase/sales/load/yesterday
     */
    @PostMapping("/load/yesterday")
    public ResponseEntity<SalesPurchaseDTO> loadFromYesterdayOrder(
            @RequestParam String locationCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();
        SalesPurchaseDTO result = salesPurchaseOrderService.loadFromYesterdayOrder(locationCode, date, currentUser);
        return ResponseEntity.ok(result);
    }

    /**
     * 帶入自定義產品清單
     * POST /api/purchase/sales/load/custom
     */
    @PostMapping("/load/custom")
    public ResponseEntity<SalesPurchaseDTO> loadFromCustomList(
            @RequestParam String locationCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();
        SalesPurchaseDTO result = salesPurchaseOrderService.loadFromCustomList(locationCode, date, currentUser);
        return ResponseEntity.ok(result);
    }

    /**
     * 帶入營業所產品清單
     * POST /api/purchase/sales/load/branch
     */
    @PostMapping("/load/branch")
    public ResponseEntity<SalesPurchaseDTO> loadFromBranchList(
            @RequestParam String locationCode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();
        SalesPurchaseDTO result = salesPurchaseOrderService.loadFromBranchList(locationCode, date, currentUser);
        return ResponseEntity.ok(result);
    }

    /**
     * 取得自定義產品清單
     * GET /api/purchase/sales/custom-list
     */
    @GetMapping("/custom-list")
    public ResponseEntity<List<SalesPurchaseListDTO>> getCustomList(@RequestParam String locationCode) {
        List<SalesPurchaseListDTO> result = salesPurchaseOrderService.getCustomList(locationCode);
        return ResponseEntity.ok(result);
    }

    /**
     * 儲存自定義產品清單
     * PUT /api/purchase/sales/custom-list
     */
    @PutMapping("/custom-list")
    public ResponseEntity<List<SalesPurchaseListDTO>> saveCustomList(
            @RequestParam String locationCode,
            @RequestBody List<SalesPurchaseListDTO> items) {
        List<SalesPurchaseListDTO> result = salesPurchaseOrderService.saveCustomList(locationCode, items);
        return ResponseEntity.ok(result);
    }
}
