package com.example.mockodsvue.purchase.controller;

import com.example.mockodsvue.purchase.model.dto.SalesPurchaseDTO;
import com.example.mockodsvue.purchase.model.dto.SalesPurchaseListDTO;
import com.example.mockodsvue.purchase.service.SalesPurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/purchase/sales")
@PreAuthorize("hasAnyRole('ADMIN', 'LEADER', 'SALES')")
public class SalesPurchaseController {

    private final SalesPurchaseOrderService salesPurchaseOrderService;

    /**
     * 查詢訂單 (若不存在則自動建立)
     * GET /api/purchase/sales?date=yyyy-MM-dd
     */
    @GetMapping("")
    public ResponseEntity<SalesPurchaseDTO> getOrder(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        SalesPurchaseDTO result = salesPurchaseOrderService.findOrCreateByCondition(
                userDetails.getUsername(), date);
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
     * POST /api/purchase/sales/load/yesterday?date=yyyy-MM-dd
     */
    @PostMapping("/load/yesterday")
    public ResponseEntity<SalesPurchaseDTO> loadFromYesterdayOrder(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                                   @AuthenticationPrincipal UserDetails userDetails) {
        SalesPurchaseDTO result = salesPurchaseOrderService.loadFromYesterdayOrder(
                userDetails.getUsername(), date);
        return ResponseEntity.ok(result);
    }

    /**
     * 帶入自定義產品清單
     * POST /api/purchase/sales/load/custom?date=yyyy-MM-dd
     */
    @PostMapping("/load/custom")
    public ResponseEntity<SalesPurchaseDTO> loadFromCustomList(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        SalesPurchaseDTO result = salesPurchaseOrderService.loadFromCustomList(userDetails.getUsername(), date);
        return ResponseEntity.ok(result);
    }

    /**
     * 帶入營業所產品清單
     * POST /api/purchase/sales/load/branch?date=yyyy-MM-dd
     */
    @PostMapping("/load/branch")
    public ResponseEntity<SalesPurchaseDTO> loadFromBranchList(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                                               @AuthenticationPrincipal UserDetails userDetails) {
        SalesPurchaseDTO result = salesPurchaseOrderService.loadFromBranchList(userDetails.getUsername(), date);
        return ResponseEntity.ok(result);
    }

    /**
     * 取得自定義產品清單
     * GET /api/purchase/sales/custom-list
     */
    @GetMapping("/custom-list")
    public ResponseEntity<List<SalesPurchaseListDTO>> getCustomList(@AuthenticationPrincipal UserDetails userDetails) {
        List<SalesPurchaseListDTO> result = salesPurchaseOrderService.getCustomList(userDetails.getUsername());
        return ResponseEntity.ok(result);
    }

    /**
     * 儲存自定義產品清單
     * PUT /api/purchase/sales/custom-list
     */
    @PutMapping("/custom-list")
    public ResponseEntity<List<SalesPurchaseListDTO>> saveCustomList(@RequestBody List<SalesPurchaseListDTO> items,
                                                                     @AuthenticationPrincipal UserDetails userDetails) {
        List<SalesPurchaseListDTO> result = salesPurchaseOrderService.saveCustomList(userDetails.getUsername(), items);
        return ResponseEntity.ok(result);
    }
}
