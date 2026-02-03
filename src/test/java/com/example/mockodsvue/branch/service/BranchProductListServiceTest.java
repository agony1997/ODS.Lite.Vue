package com.example.mockodsvue.branch.service;

import com.example.mockodsvue.shared.exception.BusinessException;
import com.example.mockodsvue.branch.model.dto.BranchProductListDTO;
import com.example.mockodsvue.branch.model.entity.Branch;
import com.example.mockodsvue.branch.model.entity.BranchProductList;
import com.example.mockodsvue.branch.repository.BranchProductListRepository;
import com.example.mockodsvue.branch.repository.BranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BranchProductListService 測試")
class BranchProductListServiceTest {

    @Mock
    private BranchProductListRepository branchProductListRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private BranchProductListService branchProductListService;

    private Branch testBranch;

    @BeforeEach
    void setUp() {
        testBranch = new Branch();
        testBranch.setId(1);
        testBranch.setBranchCode("BR01");
        testBranch.setBranchName("測試營業所");
        testBranch.setStatus("ACTIVE");
    }

    @Test
    @DisplayName("查詢營業所產品清單成功")
    void getBranchProductList_Success() {
        // given
        BranchProductList item = new BranchProductList();
        item.setBranchCode("BR01");
        item.setProductCode("P001");
        item.setProductName("測試商品");
        item.setUnit("箱");
        item.setSortOrder(1);

        when(branchRepository.findByBranchCode("BR01")).thenReturn(Optional.of(testBranch));
        when(branchProductListRepository.findByBranchCodeOrderBySortOrder("BR01")).thenReturn(List.of(item));

        // when
        List<BranchProductList> result = branchProductListService.getBranchProductList("BR01");

        // then
        assertEquals(1, result.size());
        assertEquals("P001", result.get(0).getProductCode());
        verify(branchProductListRepository).findByBranchCodeOrderBySortOrder("BR01");
    }

    @Test
    @DisplayName("查詢營業所產品清單失敗 - 營業所不存在")
    void getBranchProductList_BranchNotFound_ThrowsException() {
        // given
        when(branchRepository.findByBranchCode("BR99")).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> branchProductListService.getBranchProductList("BR99")
        );
        assertEquals("營業所不存在: BR99", exception.getMessage());
    }

    @Test
    @DisplayName("儲存營業所產品清單成功")
    void saveBranchProductList_Success() {
        // given
        BranchProductListDTO dto = new BranchProductListDTO();
        dto.setProductCode("P001");
        dto.setProductName("測試商品");
        dto.setUnit("箱");

        when(branchRepository.findByBranchCode("BR01")).thenReturn(Optional.of(testBranch));
        when(branchProductListRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        List<BranchProductList> result = branchProductListService.saveBranchProductList("BR01", List.of(dto));

        // then
        assertEquals(1, result.size());
        assertEquals("BR01", result.get(0).getBranchCode());
        assertEquals("P001", result.get(0).getProductCode());
        assertEquals(1, result.get(0).getSortOrder());
        verify(branchProductListRepository).deleteByBranchCode("BR01");
        verify(branchProductListRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("儲存營業所產品清單失敗 - 營業所不存在")
    void saveBranchProductList_BranchNotFound_ThrowsException() {
        // given
        when(branchRepository.findByBranchCode("BR99")).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> branchProductListService.saveBranchProductList("BR99", List.of())
        );
        assertEquals("營業所不存在: BR99", exception.getMessage());
        verify(branchProductListRepository, never()).deleteByBranchCode(any());
    }

    @Test
    @DisplayName("複製產品清單成功")
    void copy_Success() {
        // given
        Branch targetBranch = new Branch();
        targetBranch.setBranchCode("BR02");
        targetBranch.setBranchName("目標營業所");
        targetBranch.setStatus("ACTIVE");

        BranchProductList sourceItem = new BranchProductList();
        sourceItem.setBranchCode("BR01");
        sourceItem.setProductCode("P001");
        sourceItem.setProductName("測試商品");
        sourceItem.setUnit("箱");
        sourceItem.setSortOrder(1);

        when(branchRepository.findByBranchCode("BR01")).thenReturn(Optional.of(testBranch));
        when(branchRepository.findByBranchCode("BR02")).thenReturn(Optional.of(targetBranch));
        when(branchProductListRepository.findByBranchCodeOrderBySortOrder("BR01")).thenReturn(List.of(sourceItem));
        when(branchProductListRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        List<BranchProductList> result = branchProductListService.copy("BR01", "BR02");

        // then
        assertEquals(1, result.size());
        assertEquals("BR02", result.get(0).getBranchCode());
        assertEquals("P001", result.get(0).getProductCode());
        verify(branchProductListRepository).deleteByBranchCode("BR02");
    }

    @Test
    @DisplayName("複製產品清單失敗 - 來源營業所不存在")
    void copy_SourceBranchNotFound_ThrowsException() {
        // given
        when(branchRepository.findByBranchCode("BR99")).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> branchProductListService.copy("BR99", "BR02")
        );
        assertEquals("營業所不存在: BR99", exception.getMessage());
    }

    @Test
    @DisplayName("複製產品清單失敗 - 目標營業所不存在")
    void copy_TargetBranchNotFound_ThrowsException() {
        // given
        when(branchRepository.findByBranchCode("BR01")).thenReturn(Optional.of(testBranch));
        when(branchRepository.findByBranchCode("BR99")).thenReturn(Optional.empty());

        // when & then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> branchProductListService.copy("BR01", "BR99")
        );
        assertEquals("營業所不存在: BR99", exception.getMessage());
    }

    @Test
    @DisplayName("複製產品清單失敗 - 來源無商品")
    void copy_SourceEmpty_ThrowsException() {
        // given
        Branch targetBranch = new Branch();
        targetBranch.setBranchCode("BR02");
        targetBranch.setStatus("ACTIVE");

        when(branchRepository.findByBranchCode("BR01")).thenReturn(Optional.of(testBranch));
        when(branchRepository.findByBranchCode("BR02")).thenReturn(Optional.of(targetBranch));
        when(branchProductListRepository.findByBranchCodeOrderBySortOrder("BR01")).thenReturn(List.of());

        // when & then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> branchProductListService.copy("BR01", "BR02")
        );
        assertEquals("來源營業所沒有產品清單", exception.getMessage());
    }
}
