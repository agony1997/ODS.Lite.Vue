package com.example.mockodsvue.purchase.model.entity;

import com.example.mockodsvue.purchase.model.enums.SalesOrderDetailStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SalesPurchaseOrderDetailTest {

    @Test
    void markAsAggregated_WhenPending_TransitionsToAggregated() {
        SalesPurchaseOrderDetail detail = new SalesPurchaseOrderDetail();

        detail.markAsAggregated();

        assertEquals(SalesOrderDetailStatus.AGGREGATED, detail.getStatus());
    }

    @Test
    void markAsAggregated_WhenAlreadyAggregated_ThrowsException() {
        SalesPurchaseOrderDetail detail = new SalesPurchaseOrderDetail();
        detail.markAsAggregated();

        IllegalStateException ex = assertThrows(IllegalStateException.class, detail::markAsAggregated);
        assertEquals("此明細已彙總，不可重複彙總", ex.getMessage());
    }

    @Test
    void initializeConfirmedQty_SetsConfirmedQtyToQty() {
        SalesPurchaseOrderDetail detail = new SalesPurchaseOrderDetail();
        detail.setQty(10);
        detail.setConfirmedQty(0);

        detail.initializeConfirmedQty();

        assertEquals(10, detail.getConfirmedQty());
    }

    @Test
    void isPending_DefaultStatus_ReturnsTrue() {
        SalesPurchaseOrderDetail detail = new SalesPurchaseOrderDetail();

        assertTrue(detail.isPending());
        assertFalse(detail.isAggregated());
    }
}
