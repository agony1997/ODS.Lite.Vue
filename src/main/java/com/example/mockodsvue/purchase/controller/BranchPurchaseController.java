package com.example.mockodsvue.purchase.controller;

import com.example.mockodsvue.purchase.model.dto.AggregateRequestDTO;
import com.example.mockodsvue.purchase.model.dto.BranchPurchaseFreezeDTO;
import com.example.mockodsvue.purchase.model.dto.BranchPurchaseOrderDTO;
import com.example.mockodsvue.purchase.model.dto.BranchPurchaseSummaryDTO;
import com.example.mockodsvue.purchase.model.dto.BranchPurchaseSummaryUpdateDTO;
import com.example.mockodsvue.purchase.service.BranchPurchaseService;
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
@RequestMapping("/api/purchase/branch")
@PreAuthorize("hasAnyRole('ADMIN', 'LEADER')")
public class BranchPurchaseController {

    private final BranchPurchaseService branchPurchaseService;

    /**
     * 查詢營業所彙總資料
     * GET /api/purchase/branch/summary?branchCode=xxx&date=yyyy-MM-dd
     */
    @GetMapping("/summary")
    public ResponseEntity<BranchPurchaseSummaryDTO> getSummary(@RequestParam String branchCode,
                                                               @RequestParam
                                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        BranchPurchaseSummaryDTO result = branchPurchaseService.getSummary(branchCode, date);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新確認數量
     * PUT /api/purchase/branch/summary
     */
    @PutMapping("/summary")
    public ResponseEntity<BranchPurchaseSummaryDTO> updateConfirmedQty(@RequestBody BranchPurchaseSummaryUpdateDTO dto,
                                                                       @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();
        BranchPurchaseSummaryDTO result = branchPurchaseService.updateConfirmedQty(dto, currentUser);
        return ResponseEntity.ok(result);
    }

    /**
     * 凍結營業所
     * POST /api/purchase/branch/freeze
     */
    @PostMapping("/freeze")
    public ResponseEntity<BranchPurchaseSummaryDTO> freeze(@RequestBody BranchPurchaseFreezeDTO dto,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();
        BranchPurchaseSummaryDTO result = branchPurchaseService.freeze(dto, currentUser);
        return ResponseEntity.ok(result);
    }

    /**
     * 解除凍結
     * POST /api/purchase/branch/unfreeze
     */
    @PostMapping("/unfreeze")
    public ResponseEntity<BranchPurchaseSummaryDTO> unfreeze(@RequestBody BranchPurchaseFreezeDTO dto,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();
        BranchPurchaseSummaryDTO result = branchPurchaseService.unfreeze(dto, currentUser);
        return ResponseEntity.ok(result);
    }

    /**
     * 確認完成
     * POST /api/purchase/branch/confirm
     */
    @PostMapping("/confirm")
    public ResponseEntity<BranchPurchaseSummaryDTO> confirm(@RequestBody BranchPurchaseFreezeDTO dto,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();
        BranchPurchaseSummaryDTO result = branchPurchaseService.confirm(dto, currentUser);
        return ResponseEntity.ok(result);
    }

    /**
     * 執行彙總建立 BPO
     * POST /api/purchase/branch/aggregate
     */
    @PostMapping("/aggregate")
    public ResponseEntity<List<BranchPurchaseOrderDTO>> aggregate(@RequestBody AggregateRequestDTO dto,
                                                                  @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();
        List<BranchPurchaseOrderDTO> result = branchPurchaseService.aggregate(dto.getBranchCode(), dto.getPurchaseDate(), currentUser);
        return ResponseEntity.ok(result);
    }

    /**
     * 查詢 BPO 清單
     * GET /api/purchase/branch/bpo?branchCode=xxx&date=yyyy-MM-dd
     */
    @GetMapping("/bpo")
    public ResponseEntity<List<BranchPurchaseOrderDTO>> getBpoList(@RequestParam String branchCode,
                                                                   @RequestParam
                                                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<BranchPurchaseOrderDTO> result = branchPurchaseService.getBpoList(branchCode, date);
        return ResponseEntity.ok(result);
    }
}
