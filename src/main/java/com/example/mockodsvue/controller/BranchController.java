package com.example.mockodsvue.controller;

import com.example.mockodsvue.model.entity.branch.Branch;
import com.example.mockodsvue.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/branches")
public class BranchController {

    private final BranchRepository branchRepository;

    /**
     * 取得所有啟用的營業所清單
     * GET /api/branches
     */
    @GetMapping
    public ResponseEntity<List<Branch>> getAllEnabled() {
        List<Branch> result = branchRepository.findByStatus("ACTIVE");
        return ResponseEntity.ok(result);
    }
}
