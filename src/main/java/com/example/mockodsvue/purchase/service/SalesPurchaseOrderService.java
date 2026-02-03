package com.example.mockodsvue.purchase.service;

import com.example.mockodsvue.shared.exception.BusinessException;
import com.example.mockodsvue.purchase.mapper.SalesPurchaseMapper;
import com.example.mockodsvue.purchase.model.dto.SalesPurchaseDTO;
import com.example.mockodsvue.purchase.model.dto.SalesPurchaseListDTO;
import com.example.mockodsvue.branch.model.entity.BranchProductList;
import com.example.mockodsvue.branch.model.entity.Location;
import com.example.mockodsvue.purchase.model.entity.BranchPurchaseFrozen;
import com.example.mockodsvue.purchase.model.entity.SalesPurchaseList;
import com.example.mockodsvue.purchase.model.entity.SalesPurchaseOrder;
import com.example.mockodsvue.purchase.model.entity.SalesPurchaseOrderDetail;
import com.example.mockodsvue.purchase.model.enums.FrozenStatus;
import com.example.mockodsvue.sequence.model.enums.SequenceType;
import com.example.mockodsvue.branch.repository.BranchProductListRepository;
import com.example.mockodsvue.branch.repository.LocationRepository;
import com.example.mockodsvue.purchase.repository.BranchPurchaseFrozenRepository;
import com.example.mockodsvue.purchase.repository.SalesPurchaseListRepository;
import com.example.mockodsvue.purchase.repository.SalesPurchaseOrderDetailRepository;
import com.example.mockodsvue.purchase.repository.SalesPurchaseOrderRepository;
import com.example.mockodsvue.sequence.service.SequenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 業務員訂貨單服務
 * <p>
 * 負責處理業務員訂貨單的查詢、建立、更新，以及自定義產品清單的管理。
 * 編輯權限由 BPF (營業所凍結單) 控制。
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesPurchaseOrderService {

    private final SalesPurchaseOrderRepository orderRepository;
    private final SalesPurchaseOrderDetailRepository detailRepository;
    private final SalesPurchaseListRepository customListRepository;
    private final BranchProductListRepository branchProductRepository;
    private final BranchPurchaseFrozenRepository frozenRepository;
    private final LocationRepository locationRepository;
    private final SequenceGenerator sequenceGenerator;
    private final SalesPurchaseMapper mapper;

    /** 最小訂貨天數 (今天 + N 天後才能訂貨) */
    private static final int MIN_DAYS_AHEAD = 2;
    /** 最大訂貨天數 (今天 + N 天內才能訂貨) */
    private static final int MAX_DAYS_AHEAD = 9;

    // ==================== 公開方法 - 查詢/建立 ====================

    /**
     * 依條件查詢或建立訂貨單（透過使用者代碼自動解析儲位）
     *
     * @param userCode     當前使用者代碼
     * @param purchaseDate 訂貨日期
     * @return 訂貨單 DTO (含明細、產品名稱、前日訂購量)
     * @throws BusinessException 訂貨日期超出允許範圍、使用者無對應儲位
     */
    @Transactional
    public SalesPurchaseDTO findOrCreateByCondition(String userCode, LocalDate purchaseDate) {
        validatePurchaseDate(purchaseDate);
        Location location = getLocationByUser(userCode);

        SalesPurchaseOrder order = orderRepository
                .findByLocationCodeAndPurchaseDate(location.getLocationCode(), purchaseDate)
                .orElseGet(() -> createOrder(location, purchaseDate, userCode));

        return toDTO(order, location);
    }

    /**
     * 更新訂貨單明細
     *
     * @param dto 訂貨單 DTO (需包含 purchaseNo 及完整 details)
     * @return 更新後的訂貨單 DTO
     * @throws BusinessException 訂單不存在、訂單已凍結
     */
    @Transactional
    public SalesPurchaseDTO updateOrder(SalesPurchaseDTO dto) {
        SalesPurchaseOrder order = getOrder(dto.getPurchaseNo());
        validateNotFrozen(order.getBranchCode(), order.getPurchaseDate());

        List<SalesPurchaseOrderDetail> newDetails = mapper.toDetailEntities(dto.getPurchaseNo(), dto.getDetails());
        replaceDetails(dto.getPurchaseNo(), newDetails);

        return toDTO(order, getLocation(order.getLocationCode()), newDetails);
    }

    // ==================== 公開方法 - 載入資料 ====================

    /**
     * 從前一天訂單載入明細（透過使用者代碼自動解析儲位）
     *
     * @param userCode     當前使用者代碼
     * @param purchaseDate 訂貨日期
     * @return 載入後的訂貨單 DTO
     * @throws BusinessException 找不到前一天訂單、訂單已凍結
     */
    @Transactional
    public SalesPurchaseDTO loadFromYesterdayOrder(String userCode, LocalDate purchaseDate) {
        validatePurchaseDate(purchaseDate);
        Location location = getLocationByUser(userCode);
        String locationCode = location.getLocationCode();

        LocalDate yesterday = purchaseDate.minusDays(1);
        SalesPurchaseOrder yesterdayOrder = orderRepository
                .findByLocationCodeAndPurchaseDate(locationCode, yesterday)
                .orElseThrow(() -> new BusinessException("找不到前一天的訂單"));

        List<SalesPurchaseOrderDetail> yesterdayDetails = detailRepository
                .findByPurchaseNoOrderByItemNo(yesterdayOrder.getPurchaseNo());

        return loadDetailsWithLocation(location, purchaseDate, userCode, yesterdayDetails,
                d -> mapper.toDetailEntity(null, 0, d.getProductCode(), d.getUnit(), d.getQty(), 0));
    }

    /**
     * 從自定義產品清單載入明細（透過使用者代碼自動解析儲位）
     *
     * @param userCode     當前使用者代碼
     * @param purchaseDate 訂貨日期
     * @return 載入後的訂貨單 DTO
     * @throws BusinessException 尚未建立自定義清單、訂單已凍結
     */
    @Transactional
    public SalesPurchaseDTO loadFromCustomList(String userCode, LocalDate purchaseDate) {
        validatePurchaseDate(purchaseDate);
        Location location = getLocationByUser(userCode);
        String locationCode = location.getLocationCode();

        List<SalesPurchaseList> customList = customListRepository.findByLocationCodeOrderBySortOrder(locationCode);
        if (customList.isEmpty()) {
            throw new BusinessException("尚未建立自定義產品清單");
        }

        return loadDetailsWithLocation(location, purchaseDate, userCode, customList,
                d -> mapper.toDetailEntity(null, 0, d.getProductCode(), d.getUnit(), d.getQty(), 0));
    }

    /**
     * 從營業所產品清單載入明細（透過使用者代碼自動解析儲位）
     *
     * @param userCode     當前使用者代碼
     * @param purchaseDate 訂貨日期
     * @return 載入後的訂貨單 DTO
     * @throws BusinessException 使用者無對應儲位、訂單已凍結
     */
    @Transactional
    public SalesPurchaseDTO loadFromBranchList(String userCode, LocalDate purchaseDate) {
        validatePurchaseDate(purchaseDate);
        Location location = getLocationByUser(userCode);

        List<BranchProductList> branchList = branchProductRepository
                .findByBranchCodeOrderBySortOrder(location.getBranchCode());

        return loadDetailsWithLocation(location, purchaseDate, userCode, branchList,
                d -> mapper.toDetailEntity(null, 0, d.getProductCode(), d.getUnit(), 0, 0));
    }

    // ==================== 公開方法 - 自定義清單 ====================

    /**
     * 儲存自定義產品清單（透過使用者代碼自動解析儲位）
     *
     * @param userCode 當前使用者代碼
     * @param items    產品清單項目
     * @return 儲存後的清單 DTO
     */
    @Transactional
    public List<SalesPurchaseListDTO> saveCustomList(String userCode, List<SalesPurchaseListDTO> items) {
        Location location = getLocationByUser(userCode);
        String locationCode = location.getLocationCode();

        customListRepository.deleteByLocationCode(locationCode);

        List<SalesPurchaseList> entities = IntStream.range(0, items.size())
                .mapToObj(i -> {
                    SalesPurchaseList entity = mapper.toListEntity(items.get(i));
                    entity.setLocationCode(locationCode);
                    entity.setSortOrder(i + 1);
                    return entity;
                })
                .toList();

        return mapper.toListDTOList(customListRepository.saveAll(entities));
    }

    /**
     * 查詢自定義產品清單（透過使用者代碼自動解析儲位）
     *
     * @param userCode 當前使用者代碼
     * @return 該儲位的自定義清單 (依排序)
     */
    public List<SalesPurchaseListDTO> getCustomList(String userCode) {
        Location location = getLocationByUser(userCode);
        return mapper.toListDTOList(customListRepository.findByLocationCodeOrderBySortOrder(location.getLocationCode()));
    }

    // ==================== 私有方法 - 驗證 ====================

    /**
     * 驗證訂貨日期是否在允許範圍內
     */
    private void validatePurchaseDate(LocalDate date) {
        LocalDate today = LocalDate.now();
        LocalDate minDate = today.plusDays(MIN_DAYS_AHEAD);
        LocalDate maxDate = today.plusDays(MAX_DAYS_AHEAD);

        if (date.isBefore(minDate) || date.isAfter(maxDate)) {
            throw new BusinessException(String.format("訂貨日期必須在 %s 至 %s 之間", minDate, maxDate));
        }
    }

    /**
     * 驗證營業所是否已凍結
     */
    private void validateNotFrozen(String branchCode, LocalDate purchaseDate) {
        Optional<BranchPurchaseFrozen> frozen = frozenRepository.findByBranchCodeAndPurchaseDate(branchCode, purchaseDate);
        if (frozen.isPresent()) {
            throw new BusinessException("營業所已凍結，無法修改訂單");
        }
    }

    /**
     * 查詢凍結狀態
     */
    private FrozenStatus getFrozenStatus(String branchCode, LocalDate purchaseDate) {
        return frozenRepository.findByBranchCodeAndPurchaseDate(branchCode, purchaseDate)
                .map(BranchPurchaseFrozen::getStatus)
                .orElse(null);
    }

    // ==================== 私有方法 - 查詢 ====================

    /**
     * 依使用者代碼查詢對應的儲位
     */
    private Location getLocationByUser(String userCode) {
        return locationRepository.findByUserCode(userCode)
                .orElseThrow(() -> new BusinessException("找不到使用者對應的儲位: " + userCode));
    }

    /**
     * 依儲位代碼查詢儲位
     */
    private Location getLocation(String locationCode) {
        return locationRepository.findByLocationCode(locationCode)
                .orElseThrow(() -> new BusinessException("儲位不存在: " + locationCode));
    }

    /**
     * 依訂單編號查詢訂單
     */
    private SalesPurchaseOrder getOrder(String purchaseNo) {
        return orderRepository.findByPurchaseNo(purchaseNo)
                .orElseThrow(() -> new BusinessException("訂單不存在: " + purchaseNo));
    }

    // ==================== 私有方法 - 建立/更新 ====================

    /**
     * 建立新訂貨單
     */
    private SalesPurchaseOrder createOrder(Location location, LocalDate purchaseDate, String currentUser) {
        String purchaseNo = sequenceGenerator.generate(SequenceType.SPO, purchaseDate);

        SalesPurchaseOrder order = new SalesPurchaseOrder();
        order.setPurchaseNo(purchaseNo);
        order.setBranchCode(location.getBranchCode());
        order.setLocationCode(location.getLocationCode());
        order.setPurchaseDate(purchaseDate);
        order.setPurchaseUser(currentUser);
        order = orderRepository.save(order);

        List<BranchProductList> branchProducts = branchProductRepository
                .findByBranchCodeOrderBySortOrder(location.getBranchCode());

        List<SalesPurchaseOrderDetail> details = mapper.toDetailEntitiesFromBranch(purchaseNo, branchProducts);
        detailRepository.saveAll(details);

        return order;
    }

    /**
     * 取代訂單明細
     */
    private void replaceDetails(String purchaseNo, List<SalesPurchaseOrderDetail> newDetails) {
        detailRepository.deleteByPurchaseNo(purchaseNo);
        detailRepository.flush();
        detailRepository.saveAll(newDetails);
    }

    /**
     * 從來源清單載入明細 (已有 Location)
     */
    private <T> SalesPurchaseDTO loadDetailsWithLocation(Location location, LocalDate purchaseDate, String currentUser,
                                                          List<T> sourceList, Function<T, SalesPurchaseOrderDetail> detailMapper) {
        SalesPurchaseOrder order = orderRepository
                .findByLocationCodeAndPurchaseDate(location.getLocationCode(), purchaseDate)
                .orElseGet(() -> createOrder(location, purchaseDate, currentUser));

        validateNotFrozen(order.getBranchCode(), order.getPurchaseDate());

        List<SalesPurchaseOrderDetail> newDetails = IntStream.range(0, sourceList.size())
                .mapToObj(i -> {
                    SalesPurchaseOrderDetail detail = detailMapper.apply(sourceList.get(i));
                    detail.setPurchaseNo(order.getPurchaseNo());
                    detail.setItemNo(i + 1);
                    return detail;
                })
                .toList();

        replaceDetails(order.getPurchaseNo(), newDetails);
        return toDTO(order, location, newDetails);
    }

    // ==================== 私有方法 - 轉換 ====================

    /**
     * 將訂單轉換為 DTO (需查詢明細)
     */
    private SalesPurchaseDTO toDTO(SalesPurchaseOrder order, Location location) {
        List<SalesPurchaseOrderDetail> details = detailRepository
                .findByPurchaseNoOrderByItemNo(order.getPurchaseNo());
        return toDTO(order, location, details);
    }

    /**
     * 將訂單轉換為 DTO (已有明細)
     */
    private SalesPurchaseDTO toDTO(SalesPurchaseOrder order, Location location, List<SalesPurchaseOrderDetail> details) {
        Map<String, BranchProductList> productMap = branchProductRepository
                .findByBranchCodeOrderBySortOrder(location.getBranchCode()).stream()
                .collect(Collectors.toMap(
                        p -> mapper.productKey(p.getProductCode(), p.getUnit()),
                        Function.identity(),
                        (a, b) -> a));

        Map<String, Integer> lastQtyMap = getLastQtyMap(order.getLocationCode(), order.getPurchaseDate());

        SalesPurchaseDTO dto = mapper.toDTO(order, details, productMap, lastQtyMap);
        dto.setFrozenStatus(getFrozenStatus(order.getBranchCode(), order.getPurchaseDate()));
        return dto;
    }

    /**
     * 查詢前一天的訂購數量 Map
     */
    private Map<String, Integer> getLastQtyMap(String locationCode, LocalDate purchaseDate) {
        LocalDate yesterday = purchaseDate.minusDays(1);
        return orderRepository.findByLocationCodeAndPurchaseDate(locationCode, yesterday)
                .map(yesterdayOrder -> detailRepository.findByPurchaseNoOrderByItemNo(yesterdayOrder.getPurchaseNo()).stream()
                        .collect(Collectors.toMap(
                                d -> mapper.productKey(d.getProductCode(), d.getUnit()),
                                SalesPurchaseOrderDetail::getQty,
                                (a, b) -> a)))
                .orElse(Map.of());
    }
}
