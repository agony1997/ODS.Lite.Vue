package com.example.mockodsvue.service;

import com.example.mockodsvue.mapper.BranchPurchaseMapper;
import com.example.mockodsvue.model.dto.BranchPurchaseFreezeDTO;
import com.example.mockodsvue.model.dto.BranchPurchaseOrderDTO;
import com.example.mockodsvue.model.dto.BranchPurchaseSummaryDTO;
import com.example.mockodsvue.model.dto.BranchPurchaseSummaryDTO.LocationInfoDTO;
import com.example.mockodsvue.model.dto.BranchPurchaseSummaryDTO.SummaryDetailDTO;
import com.example.mockodsvue.model.dto.BranchPurchaseSummaryUpdateDTO;
import com.example.mockodsvue.model.entity.branch.Location;
import com.example.mockodsvue.model.entity.master.ProductFactory;
import com.example.mockodsvue.model.entity.purchase.*;
import com.example.mockodsvue.model.enums.DeliveryStatus;
import com.example.mockodsvue.model.enums.FrozenStatus;
import com.example.mockodsvue.model.enums.SalesOrderDetailStatus;
import com.example.mockodsvue.model.enums.SequenceType;
import com.example.mockodsvue.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchPurchaseService {

    private final SalesPurchaseOrderRepository spoRepository;
    private final SalesPurchaseOrderDetailRepository spodRepository;
    private final BranchPurchaseFrozenRepository bpfRepository;
    private final BranchPurchaseOrderRepository bpoRepository;
    private final BranchPurchaseOrderDetailRepository bpodRepository;
    private final LocationRepository locationRepository;
    private final ProductFactoryRepository productFactoryRepository;
    private final SequenceGenerator sequenceGenerator;
    private final BranchPurchaseMapper mapper;

    /**
     * 查詢營業所彙總資料 (橫向展開儲位)
     */
    @Transactional(readOnly = true)
    public BranchPurchaseSummaryDTO getSummary(String branchCode, LocalDate purchaseDate) {
        // 查詢凍結狀態
        FrozenStatus frozenStatus = bpfRepository.findByBranchCodeAndPurchaseDate(branchCode, purchaseDate)
                .map(BranchPurchaseFrozen::getStatus)
                .orElse(null);

        // 查詢營業所下所有儲位
        List<Location> locations = locationRepository.findByBranchCode(branchCode);
        List<LocationInfoDTO> locationInfoList = mapper.toLocationInfoDTOList(locations);

        // 查詢該營業所當天所有 SPO
        List<SalesPurchaseOrder> spoList = spoRepository.findByBranchCodeAndPurchaseDate(branchCode, purchaseDate);

        if (spoList.isEmpty()) {
            return BranchPurchaseSummaryDTO.builder()
                    .branchCode(branchCode)
                    .purchaseDate(purchaseDate)
                    .frozenStatus(frozenStatus)
                    .locations(locationInfoList)
                    .details(Collections.emptyList())
                    .build();
        }

        // 建立 locationCode -> purchaseNo 對應
        Map<String, String> locationToPurchaseNo = spoList.stream()
                .collect(Collectors.toMap(SalesPurchaseOrder::getLocationCode, SalesPurchaseOrder::getPurchaseNo));

        // 查詢所有 SPOD
        List<String> purchaseNos = spoList.stream().map(SalesPurchaseOrder::getPurchaseNo).toList();
        List<SalesPurchaseOrderDetail> spodList = spodRepository.findByPurchaseNoIn(purchaseNos);

        // 建立 purchaseNo -> locationCode 對應
        Map<String, String> purchaseNoToLocation = spoList.stream()
                .collect(Collectors.toMap(SalesPurchaseOrder::getPurchaseNo, SalesPurchaseOrder::getLocationCode));

        // 依產品 (productCode + unit) 分組彙總
        Map<String, List<SalesPurchaseOrderDetail>> groupedByProduct = spodList.stream()
                .collect(Collectors.groupingBy(d -> mapper.productKey(d.getProductCode(), d.getUnit())));

        List<SummaryDetailDTO> details = new ArrayList<>();
        for (Map.Entry<String, List<SalesPurchaseOrderDetail>> entry : groupedByProduct.entrySet()) {
            List<SalesPurchaseOrderDetail> productDetails = entry.getValue();
            SalesPurchaseOrderDetail first = productDetails.get(0);

            // 計算總數量
            int totalQty = productDetails.stream().mapToInt(SalesPurchaseOrderDetail::getQty).sum();
            int confirmedQty = productDetails.stream().mapToInt(SalesPurchaseOrderDetail::getConfirmedQty).sum();

            // 建立各儲位數量對應
            Map<String, Integer> locationQtyMap = new LinkedHashMap<>();
            for (SalesPurchaseOrderDetail detail : productDetails) {
                String locationCode = purchaseNoToLocation.get(detail.getPurchaseNo());
                locationQtyMap.put(locationCode, detail.getQty());
            }

            details.add(SummaryDetailDTO.builder()
                    .productCode(first.getProductCode())
                    .productName(first.getProductName())
                    .unit(first.getUnit())
                    .confirmedQty(confirmedQty)
                    .totalQty(totalQty)
                    .diffQty(confirmedQty - totalQty)
                    .locationQtyMap(locationQtyMap)
                    .build());
        }

        // 依產品代碼排序
        details.sort(Comparator.comparing(SummaryDetailDTO::getProductCode));

        return BranchPurchaseSummaryDTO.builder()
                .branchCode(branchCode)
                .purchaseDate(purchaseDate)
                .frozenStatus(frozenStatus)
                .locations(locationInfoList)
                .details(details)
                .build();
    }

    /**
     * 更新確認數量 (依原始 qty 比例分配回各 SPOD)
     */
    @Transactional
    public BranchPurchaseSummaryDTO updateConfirmedQty(BranchPurchaseSummaryUpdateDTO updateDTO, String currentUser) {
        String branchCode = updateDTO.getBranchCode();
        LocalDate purchaseDate = updateDTO.getPurchaseDate();

        // 檢查凍結狀態 (必須是 FROZEN 才能編輯)
        BranchPurchaseFrozen bpf = bpfRepository.findByBranchCodeAndPurchaseDate(branchCode, purchaseDate)
                .orElseThrow(() -> new IllegalStateException("營業所尚未凍結，無法調整確認數量"));

        if (bpf.getStatus() != FrozenStatus.FROZEN) {
            throw new IllegalStateException("營業所已確認，無法再調整確認數量");
        }

        // 查詢所有 SPO
        List<SalesPurchaseOrder> spoList = spoRepository.findByBranchCodeAndPurchaseDate(branchCode, purchaseDate);
        List<String> purchaseNos = spoList.stream().map(SalesPurchaseOrder::getPurchaseNo).toList();

        // 查詢所有 SPOD
        List<SalesPurchaseOrderDetail> spodList = spodRepository.findByPurchaseNoIn(purchaseNos);

        // 依產品分組
        Map<String, List<SalesPurchaseOrderDetail>> groupedByProduct = spodList.stream()
                .collect(Collectors.groupingBy(d -> mapper.productKey(d.getProductCode(), d.getUnit())));

        // 更新每個產品的確認數量
        for (BranchPurchaseSummaryUpdateDTO.UpdateDetailDTO updateDetail : updateDTO.getDetails()) {
            String key = mapper.productKey(updateDetail.getProductCode(), updateDetail.getUnit());
            List<SalesPurchaseOrderDetail> productDetails = groupedByProduct.get(key);

            if (productDetails == null || productDetails.isEmpty()) {
                continue;
            }

            int newConfirmedQty = updateDetail.getConfirmedQty();
            int totalOriginalQty = productDetails.stream().mapToInt(SalesPurchaseOrderDetail::getQty).sum();

            if (totalOriginalQty == 0) {
                // 若原始數量為 0，平均分配
                int avgQty = newConfirmedQty / productDetails.size();
                int remainder = newConfirmedQty % productDetails.size();
                for (int i = 0; i < productDetails.size(); i++) {
                    productDetails.get(i).setConfirmedQty(avgQty + (i < remainder ? 1 : 0));
                }
            } else {
                // 依原始 qty 比例分配
                int allocated = 0;
                for (int i = 0; i < productDetails.size() - 1; i++) {
                    SalesPurchaseOrderDetail detail = productDetails.get(i);
                    int proportionalQty = (int) Math.round((double) detail.getQty() / totalOriginalQty * newConfirmedQty);
                    detail.setConfirmedQty(proportionalQty);
                    allocated += proportionalQty;
                }
                // 最後一筆分配剩餘數量 (避免四捨五入誤差)
                productDetails.get(productDetails.size() - 1).setConfirmedQty(newConfirmedQty - allocated);
            }
        }

        spodRepository.saveAll(spodList);

        return getSummary(branchCode, purchaseDate);
    }

    /**
     * 凍結營業所
     */
    @Transactional
    public BranchPurchaseSummaryDTO freeze(BranchPurchaseFreezeDTO dto, String currentUser) {
        String branchCode = dto.getBranchCode();
        LocalDate purchaseDate = dto.getPurchaseDate();

        // 檢查是否已存在凍結記錄
        Optional<BranchPurchaseFrozen> existingOpt = bpfRepository.findByBranchCodeAndPurchaseDate(branchCode, purchaseDate);
        if (existingOpt.isPresent()) {
            throw new IllegalStateException("營業所已經凍結");
        }

        // 建立凍結記錄
        BranchPurchaseFrozen bpf = new BranchPurchaseFrozen();
        bpf.setBranchCode(branchCode);
        bpf.setPurchaseDate(purchaseDate);
        bpf.setStatus(FrozenStatus.FROZEN);
        bpf.setFrozenAt(LocalDateTime.now());
        bpf.setFrozenBy(currentUser);
        bpfRepository.save(bpf);

        // 初始化 confirmedQty = qty
        List<SalesPurchaseOrder> spoList = spoRepository.findByBranchCodeAndPurchaseDate(branchCode, purchaseDate);
        List<String> purchaseNos = spoList.stream().map(SalesPurchaseOrder::getPurchaseNo).toList();
        List<SalesPurchaseOrderDetail> spodList = spodRepository.findByPurchaseNoIn(purchaseNos);

        for (SalesPurchaseOrderDetail detail : spodList) {
            detail.setConfirmedQty(detail.getQty());
        }
        spodRepository.saveAll(spodList);

        return getSummary(branchCode, purchaseDate);
    }

    /**
     * 解除凍結
     */
    @Transactional
    public BranchPurchaseSummaryDTO unfreeze(BranchPurchaseFreezeDTO dto, String currentUser) {
        String branchCode = dto.getBranchCode();
        LocalDate purchaseDate = dto.getPurchaseDate();

        // 檢查凍結狀態
        BranchPurchaseFrozen bpf = bpfRepository.findByBranchCodeAndPurchaseDate(branchCode, purchaseDate)
                .orElseThrow(() -> new IllegalStateException("營業所尚未凍結"));

        if (bpf.getStatus() == FrozenStatus.CONFIRMED) {
            throw new IllegalStateException("營業所已確認，無法解除凍結");
        }

        // 刪除凍結記錄
        bpfRepository.delete(bpf);

        return getSummary(branchCode, purchaseDate);
    }

    /**
     * 確認完成
     */
    @Transactional
    public BranchPurchaseSummaryDTO confirm(BranchPurchaseFreezeDTO dto, String currentUser) {
        String branchCode = dto.getBranchCode();
        LocalDate purchaseDate = dto.getPurchaseDate();

        // 檢查凍結狀態
        BranchPurchaseFrozen bpf = bpfRepository.findByBranchCodeAndPurchaseDate(branchCode, purchaseDate)
                .orElseThrow(() -> new IllegalStateException("營業所尚未凍結"));

        if (bpf.getStatus() == FrozenStatus.CONFIRMED) {
            throw new IllegalStateException("營業所已經確認");
        }

        // 更新為已確認
        bpf.setStatus(FrozenStatus.CONFIRMED);
        bpf.setConfirmedAt(LocalDateTime.now());
        bpf.setConfirmedBy(currentUser);
        bpfRepository.save(bpf);

        return getSummary(branchCode, purchaseDate);
    }

    /**
     * 執行彙總建立 BPO
     */
    @Transactional
    public List<BranchPurchaseOrderDTO> aggregate(String branchCode, LocalDate purchaseDate, String currentUser) {
        // 1. 驗證 BPF.status = CONFIRMED
        BranchPurchaseFrozen bpf = bpfRepository.findByBranchCodeAndPurchaseDate(branchCode, purchaseDate)
                .orElseThrow(() -> new IllegalStateException("營業所尚未凍結"));

        if (bpf.getStatus() != FrozenStatus.CONFIRMED) {
            throw new IllegalStateException("營業所尚未確認，無法彙總");
        }

        // 2. 查詢該營業所所有 SPOD (status=PENDING)
        List<SalesPurchaseOrder> spoList = spoRepository.findByBranchCodeAndPurchaseDate(branchCode, purchaseDate);
        List<String> purchaseNos = spoList.stream().map(SalesPurchaseOrder::getPurchaseNo).toList();
        List<SalesPurchaseOrderDetail> spodList = spodRepository.findByPurchaseNoInAndStatus(purchaseNos, SalesOrderDetailStatus.PENDING);

        if (spodList.isEmpty()) {
            throw new IllegalStateException("沒有待彙總的訂單明細");
        }

        // 3. 透過 ProductFactory 取得每個產品的工廠
        Map<String, String> productToFactory = new HashMap<>();
        for (SalesPurchaseOrderDetail spod : spodList) {
            if (!productToFactory.containsKey(spod.getProductCode())) {
                String factoryCode = productFactoryRepository.findByProductCodeAndIsDefaultTrue(spod.getProductCode())
                        .map(ProductFactory::getFactoryCode)
                        .orElse("DEFAULT"); // 若無設定預設工廠，使用 DEFAULT
                productToFactory.put(spod.getProductCode(), factoryCode);
            }
        }

        // 4. 依工廠分組 SPOD
        Map<String, List<SalesPurchaseOrderDetail>> groupedByFactory = spodList.stream()
                .collect(Collectors.groupingBy(spod -> productToFactory.get(spod.getProductCode())));

        // 5. 每個工廠建立一張 BPO
        List<BranchPurchaseOrder> createdBpoList = new ArrayList<>();

        for (Map.Entry<String, List<SalesPurchaseOrderDetail>> entry : groupedByFactory.entrySet()) {
            String factoryCode = entry.getKey();
            List<SalesPurchaseOrderDetail> factorySpodList = entry.getValue();

            // 使用統一的序號產生器產生 BPO 單號
            String bpoNo = sequenceGenerator.generate(SequenceType.BPO, purchaseDate);

            // 建立 BPO
            BranchPurchaseOrder bpo = new BranchPurchaseOrder();
            bpo.setBpoNo(bpoNo);
            bpo.setBranchCode(branchCode);
            bpo.setFactoryCode(factoryCode);
            bpo.setPurchaseDate(purchaseDate);
            bpo.setStatus(DeliveryStatus.PENDING);
            bpo.setCreatedBy(currentUser);
            bpoRepository.save(bpo);
            createdBpoList.add(bpo);

            // 依產品分組計算 BPOD.qty = 同產品 SPOD.confirmedQty 加總
            Map<String, List<SalesPurchaseOrderDetail>> groupedByProduct = factorySpodList.stream()
                    .collect(Collectors.groupingBy(d -> mapper.productKey(d.getProductCode(), d.getUnit())));

            int itemNo = 1;
            for (Map.Entry<String, List<SalesPurchaseOrderDetail>> productEntry : groupedByProduct.entrySet()) {
                List<SalesPurchaseOrderDetail> productDetails = productEntry.getValue();
                SalesPurchaseOrderDetail first = productDetails.get(0);
                int totalConfirmedQty = productDetails.stream().mapToInt(SalesPurchaseOrderDetail::getConfirmedQty).sum();

                BranchPurchaseOrderDetail bpod = new BranchPurchaseOrderDetail();
                bpod.setBpoNo(bpoNo);
                bpod.setItemNo(itemNo++);
                bpod.setProductCode(first.getProductCode());
                bpod.setProductName(first.getProductName() != null ? first.getProductName() : "");
                bpod.setUnit(first.getUnit());
                bpod.setQty(totalConfirmedQty);
                bpodRepository.save(bpod);
            }
        }

        // 6. 更新所有 SPOD.status = AGGREGATED
        for (SalesPurchaseOrderDetail spod : spodList) {
            spod.setStatus(SalesOrderDetailStatus.AGGREGATED);
        }
        spodRepository.saveAll(spodList);

        // 回傳建立的 BPO 清單
        return getBpoList(branchCode, purchaseDate);
    }

    /**
     * 查詢 BPO 清單
     */
    @Transactional(readOnly = true)
    public List<BranchPurchaseOrderDTO> getBpoList(String branchCode, LocalDate purchaseDate) {
        List<BranchPurchaseOrder> bpoList = bpoRepository.findByBranchCodeAndPurchaseDate(branchCode, purchaseDate);

        return bpoList.stream().map(bpo -> {
            List<BranchPurchaseOrderDetail> details = bpodRepository.findByBpoNoOrderByItemNo(bpo.getBpoNo());
            return mapper.toBpoDTO(bpo, details);
        }).toList();
    }

}
