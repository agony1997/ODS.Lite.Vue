package com.example.mockodsvue.service;

import com.example.mockodsvue.exception.BusinessException;
import com.example.mockodsvue.mapper.SalesPurchaseMapper;
import com.example.mockodsvue.model.dto.SalesPurchaseDTO;
import com.example.mockodsvue.model.dto.SalesPurchaseListDTO;
import com.example.mockodsvue.model.entity.branch.BranchProductList;
import com.example.mockodsvue.model.entity.branch.Location;
import com.example.mockodsvue.model.entity.purchase.BranchPurchaseFrozen;
import com.example.mockodsvue.model.entity.purchase.SalesPurchaseList;
import com.example.mockodsvue.model.entity.purchase.SalesPurchaseOrder;
import com.example.mockodsvue.model.entity.purchase.SalesPurchaseOrderDetail;
import com.example.mockodsvue.model.enums.FrozenStatus;
import com.example.mockodsvue.model.enums.SequenceType;
import com.example.mockodsvue.repository.*;
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
     * 依條件查詢或建立訂貨單
     * <p>
     * 若該儲位在指定日期已有訂單則返回，否則建立新訂單並預設載入營業所產品清單。
     * </p>
     *
     * @param locationCode 儲位代碼
     * @param purchaseDate 訂貨日期
     * @param currentUser  當前使用者
     * @return 訂貨單 DTO (含明細、產品名稱、前日訂購量)
     * @throws BusinessException 訂貨日期超出允許範圍、儲位不存在
     */
    @Transactional
    public SalesPurchaseDTO findOrCreateByCondition(String locationCode, LocalDate purchaseDate, String currentUser) {
        validatePurchaseDate(purchaseDate);
        Location location = getLocation(locationCode);

        SalesPurchaseOrder order = orderRepository
                .findByLocationCodeAndPurchaseDate(locationCode, purchaseDate)
                .orElseGet(() -> createOrder(location, purchaseDate, currentUser));

        return toDTO(order, location);
    }

    /**
     * 更新訂貨單明細
     * <p>
     * 以傳入的明細列表完整取代現有明細 (先刪後存)。
     * </p>
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
     * 從前一天訂單載入明細
     * <p>
     * 複製前一天同儲位訂單的明細 (保留數量，清除確認數量)。
     * </p>
     *
     * @param locationCode 儲位代碼
     * @param purchaseDate 訂貨日期
     * @param currentUser  當前使用者
     * @return 載入後的訂貨單 DTO
     * @throws BusinessException 找不到前一天訂單、訂單已凍結
     */
    @Transactional
    public SalesPurchaseDTO loadFromYesterdayOrder(String locationCode, LocalDate purchaseDate, String currentUser) {
        validatePurchaseDate(purchaseDate);

        LocalDate yesterday = purchaseDate.minusDays(1);
        SalesPurchaseOrder yesterdayOrder = orderRepository
                .findByLocationCodeAndPurchaseDate(locationCode, yesterday)
                .orElseThrow(() -> new BusinessException("找不到前一天的訂單"));

        List<SalesPurchaseOrderDetail> yesterdayDetails = detailRepository
                .findByPurchaseNoOrderByItemNo(yesterdayOrder.getPurchaseNo());

        return loadDetails(locationCode, purchaseDate, currentUser, yesterdayDetails,
                d -> mapper.toDetailEntity(null, 0, d.getProductCode(), d.getUnit(), d.getQty(), 0));
    }

    /**
     * 從自定義產品清單載入明細
     * <p>
     * 以該儲位的自定義清單建立明細 (保留數量，清除確認數量)。
     * </p>
     *
     * @param locationCode 儲位代碼
     * @param purchaseDate 訂貨日期
     * @param currentUser  當前使用者
     * @return 載入後的訂貨單 DTO
     * @throws BusinessException 尚未建立自定義清單、訂單已凍結
     */
    @Transactional
    public SalesPurchaseDTO loadFromCustomList(String locationCode, LocalDate purchaseDate, String currentUser) {
        validatePurchaseDate(purchaseDate);

        List<SalesPurchaseList> customList = customListRepository.findByLocationCodeOrderBySortOrder(locationCode);
        if (customList.isEmpty()) {
            throw new BusinessException("尚未建立自定義產品清單");
        }

        return loadDetails(locationCode, purchaseDate, currentUser, customList,
                d -> mapper.toDetailEntity(null, 0, d.getProductCode(), d.getUnit(), d.getQty(), 0));
    }

    /**
     * 從營業所產品清單載入明細
     * <p>
     * 以該儲位所屬營業所的產品清單建立明細 (數量皆為 0)。
     * </p>
     *
     * @param locationCode 儲位代碼
     * @param purchaseDate 訂貨日期
     * @param currentUser  當前使用者
     * @return 載入後的訂貨單 DTO
     * @throws BusinessException 儲位不存在、訂單已凍結
     */
    @Transactional
    public SalesPurchaseDTO loadFromBranchList(String locationCode, LocalDate purchaseDate, String currentUser) {
        validatePurchaseDate(purchaseDate);

        Location location = getLocation(locationCode);
        List<BranchProductList> branchList = branchProductRepository
                .findByBranchCodeOrderBySortOrder(location.getBranchCode());

        return loadDetailsWithLocation(location, purchaseDate, currentUser, branchList,
                d -> mapper.toDetailEntity(null, 0, d.getProductCode(), d.getUnit(), 0, 0));
    }

    // ==================== 公開方法 - 自定義清單 ====================

    /**
     * 儲存自定義產品清單
     * <p>
     * 以傳入的清單完整取代該儲位的自定義清單 (先刪後存)。
     * </p>
     *
     * @param locationCode 儲位代碼
     * @param items        產品清單項目
     * @return 儲存後的清單 DTO
     */
    @Transactional
    public List<SalesPurchaseListDTO> saveCustomList(String locationCode, List<SalesPurchaseListDTO> items) {
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
     * 查詢自定義產品清單
     *
     * @param locationCode 儲位代碼
     * @return 該儲位的自定義清單 (依排序)
     */
    public List<SalesPurchaseListDTO> getCustomList(String locationCode) {
        return mapper.toListDTOList(customListRepository.findByLocationCodeOrderBySortOrder(locationCode));
    }

    // ==================== 私有方法 - 驗證 ====================

    /**
     * 驗證訂貨日期是否在允許範圍內
     *
     * @param date 訂貨日期
     * @throws BusinessException 日期超出 [今天+MIN_DAYS_AHEAD, 今天+MAX_DAYS_AHEAD] 範圍
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
     * <p>
     * 透過查詢 BPF (營業所凍結單) 判斷是否可編輯。
     * BPF 不存在 = 可編輯，BPF 存在 = 已凍結不可編輯
     * </p>
     *
     * @param branchCode   營業所代碼
     * @param purchaseDate 訂貨日期
     * @throws BusinessException 營業所已凍結
     */
    private void validateNotFrozen(String branchCode, LocalDate purchaseDate) {
        Optional<BranchPurchaseFrozen> frozen = frozenRepository.findByBranchCodeAndPurchaseDate(branchCode, purchaseDate);
        if (frozen.isPresent()) {
            throw new BusinessException("營業所已凍結，無法修改訂單");
        }
    }

    /**
     * 查詢凍結狀態
     *
     * @param branchCode   營業所代碼
     * @param purchaseDate 訂貨日期
     * @return 凍結狀態，null 表示未凍結
     */
    private FrozenStatus getFrozenStatus(String branchCode, LocalDate purchaseDate) {
        return frozenRepository.findByBranchCodeAndPurchaseDate(branchCode, purchaseDate)
                .map(BranchPurchaseFrozen::getStatus)
                .orElse(null);
    }

    // ==================== 私有方法 - 查詢 ====================

    /**
     * 依儲位代碼查詢儲位
     *
     * @param locationCode 儲位代碼
     * @return 儲位實體
     * @throws BusinessException 儲位不存在
     */
    private Location getLocation(String locationCode) {
        return locationRepository.findByLocationCode(locationCode)
                .orElseThrow(() -> new BusinessException("儲位不存在: " + locationCode));
    }

    /**
     * 依訂單編號查詢訂單
     *
     * @param purchaseNo 訂單編號
     * @return 訂貨單實體
     * @throws BusinessException 訂單不存在
     */
    private SalesPurchaseOrder getOrder(String purchaseNo) {
        return orderRepository.findByPurchaseNo(purchaseNo)
                .orElseThrow(() -> new BusinessException("訂單不存在: " + purchaseNo));
    }

    // ==================== 私有方法 - 建立/更新 ====================

    /**
     * 建立新訂貨單
     * <p>
     * 自動產生訂單編號，並以營業所產品清單初始化明細 (數量皆為 0)。
     * </p>
     *
     * @param location     儲位實體
     * @param purchaseDate 訂貨日期
     * @param currentUser  當前使用者
     * @return 新建的訂貨單實體
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
     * <p>
     * 刪除該訂單所有明細後，批次新增傳入的明細。
     * </p>
     *
     * @param purchaseNo 訂單編號
     * @param newDetails 新明細列表
     */
    private void replaceDetails(String purchaseNo, List<SalesPurchaseOrderDetail> newDetails) {
        detailRepository.deleteByPurchaseNo(purchaseNo);
        detailRepository.saveAll(newDetails);
    }

    /**
     * 從來源清單載入明細 (需查詢 Location)
     *
     * @param locationCode 儲位代碼
     * @param purchaseDate 訂貨日期
     * @param currentUser  當前使用者
     * @param sourceList   來源資料列表
     * @param detailMapper 將來源項目轉換為明細的函數
     * @param <T>          來源項目型別
     * @return 載入後的訂貨單 DTO
     */
    private <T> SalesPurchaseDTO loadDetails(String locationCode, LocalDate purchaseDate, String currentUser,
                                              List<T> sourceList, Function<T, SalesPurchaseOrderDetail> detailMapper) {
        return loadDetailsWithLocation(getLocation(locationCode), purchaseDate, currentUser, sourceList, detailMapper);
    }

    /**
     * 從來源清單載入明細 (已有 Location)
     * <p>
     * 若訂單不存在則建立，接著以來源清單取代明細。
     * </p>
     *
     * @param location     儲位實體
     * @param purchaseDate 訂貨日期
     * @param currentUser  當前使用者
     * @param sourceList   來源資料列表
     * @param detailMapper 將來源項目轉換為明細的函數 (purchaseNo 和 itemNo 會被覆寫)
     * @param <T>          來源項目型別
     * @return 載入後的訂貨單 DTO
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
     *
     * @param order    訂貨單實體
     * @param location 儲位實體
     * @return 完整的訂貨單 DTO
     */
    private SalesPurchaseDTO toDTO(SalesPurchaseOrder order, Location location) {
        List<SalesPurchaseOrderDetail> details = detailRepository
                .findByPurchaseNoOrderByItemNo(order.getPurchaseNo());
        return toDTO(order, location, details);
    }

    /**
     * 將訂單轉換為 DTO (已有明細)
     * <p>
     * 補充產品名稱、前日訂購量及凍結狀態後，委託 Mapper 組合成完整 DTO。
     * </p>
     *
     * @param order    訂貨單實體
     * @param location 儲位實體 (用於查詢營業所產品資訊)
     * @param details  訂單明細列表
     * @return 完整的訂貨單 DTO
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
     * <p>
     * Key 為 productCode-unit，Value 為訂購數量。
     * </p>
     *
     * @param locationCode 儲位代碼
     * @param purchaseDate 訂貨日期
     * @return 前日訂購量 Map (若無前日訂單則返回空 Map)
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
