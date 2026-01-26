package com.example.mockodsvue.controller;

import com.example.mockodsvue.model.dto.BranchProductListDTO;
import com.example.mockodsvue.model.entity.branch.BranchProductList;
import com.example.mockodsvue.service.BranchProductListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/branch-product-list")
public class BranchProductListController {

    private final BranchProductListService branchProductListService;

    /**
     * 查詢營業所產品清單
     * GET /api/branch-product-list/{branchCode}
     */
    @GetMapping("/{branchCode}")
    public ResponseEntity<List<BranchProductList>> getByBranchCode(@PathVariable String branchCode) {
        List<BranchProductList> result = branchProductListService.getBranchProductList(branchCode);
        return ResponseEntity.ok(result);
    }

    /**
     * 儲存營業所產品清單 (全量替換模式)
     * PUT /api/branch-product-list/{branchCode}
     */
    @PutMapping("/{branchCode}")
    public ResponseEntity<List<BranchProductList>> save(
            @PathVariable String branchCode,
            @RequestBody List<BranchProductListDTO> list) {
        List<BranchProductList> result = branchProductListService.saveBranchProductList(branchCode, list);
        return ResponseEntity.ok(result);
    }

    /**
     * 複製產品清單到其他營業所
     * POST /api/branch-product-list/copy
     */
    @PostMapping("/copy")
    public ResponseEntity<List<BranchProductList>> copy(
            @RequestParam String from,
            @RequestParam String to) {
        List<BranchProductList> result = branchProductListService.copy(from, to);
        return ResponseEntity.ok(result);
    }
}
