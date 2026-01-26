package com.example.mockodsvue.service;

import com.example.mockodsvue.exception.BusinessException;
import com.example.mockodsvue.model.dto.BranchProductListDTO;
import com.example.mockodsvue.model.entity.branch.BranchProductList;
import com.example.mockodsvue.repository.BranchProductListRepository;
import com.example.mockodsvue.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BranchProductListService {

    private final BranchProductListRepository branchProductListRepository;
    private final BranchRepository branchRepository;

    /**
     * 查詢營業所產品清單 (依 sortOrder 排序)
     *
     * @param branchCode 營業所代碼
     * @return 產品清單
     */
    public List<BranchProductList> getBranchProductList(String branchCode) {
        validateBranchExists(branchCode);
        return branchProductListRepository.findByBranchCodeOrderBySortOrder(branchCode);
    }

    /**
     * 儲存營業所產品清單 (全量替換模式)
     *
     * @param branchCode 營業所代碼
     * @param list 產品清單 DTO
     * @return 更新後的產品清單
     */
    @Transactional
    public List<BranchProductList> saveBranchProductList(String branchCode, List<BranchProductListDTO> list) {
        validateBranchExists(branchCode);

        // 1. 刪除該營業所現有清單
        branchProductListRepository.deleteByBranchCode(branchCode);

        if (list == null || list.isEmpty()) {
            return List.of();
        }

        // 2. 依據傳入 List 的順序重新建立 Entity (自動產生 sortOrder)
        List<BranchProductList> entities = IntStream.range(0, list.size())
                .mapToObj(i -> toEntity(branchCode, list.get(i), i + 1))
                .toList();

        return branchProductListRepository.saveAll(entities);
    }

    /**
     * 複製產品清單到另一營業所
     *
     * @param fromBranchCode 來源營業所代碼
     * @param toBranchCode   目標營業所代碼
     * @return 複製後的產品清單
     */
    @Transactional
    public List<BranchProductList> copy(String fromBranchCode, String toBranchCode) {
        validateBranchExists(fromBranchCode);
        validateBranchExists(toBranchCode);

        List<BranchProductList> sourceList = branchProductListRepository.findByBranchCodeOrderBySortOrder(fromBranchCode);
        if (sourceList.isEmpty()) {
            throw new BusinessException("來源營業所沒有產品清單");
        }

        branchProductListRepository.deleteByBranchCode(toBranchCode);

        List<BranchProductList> entities = sourceList.stream()
                .map(source -> {
                    BranchProductList target = new BranchProductList();
                    target.setBranchCode(toBranchCode);
                    target.setProductCode(source.getProductCode());
                    target.setProductName(source.getProductName());
                    target.setUnit(source.getUnit());
                    target.setSortOrder(source.getSortOrder());
                    return target;
                })
                .toList();

        return branchProductListRepository.saveAll(entities);
    }

    private void validateBranchExists(String branchCode) {
        branchRepository.findByBranchCode(branchCode)
                .orElseThrow(() -> new BusinessException("營業所不存在: " + branchCode));
    }

    private BranchProductList toEntity(String branchCode, BranchProductListDTO dto, int sortOrder) {
        BranchProductList entity = new BranchProductList();
        entity.setBranchCode(branchCode);
        entity.setProductCode(dto.getProductCode());
        entity.setProductName(dto.getProductName());
        entity.setUnit(dto.getUnit());
        entity.setSortOrder(sortOrder);
        return entity;
    }
}
