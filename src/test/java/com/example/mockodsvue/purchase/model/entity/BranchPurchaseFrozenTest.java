package com.example.mockodsvue.purchase.model.entity;

import com.example.mockodsvue.purchase.model.enums.FrozenStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BranchPurchaseFrozenTest {

    @Test
    void createFrozen_SetsAllFields() {
        BranchPurchaseFrozen bpf = BranchPurchaseFrozen.createFrozen("BR001", LocalDate.of(2025, 1, 15), "user1");

        assertEquals("BR001", bpf.getBranchCode());
        assertEquals(LocalDate.of(2025, 1, 15), bpf.getPurchaseDate());
        assertEquals(FrozenStatus.FROZEN, bpf.getStatus());
        assertNotNull(bpf.getFrozenAt());
        assertEquals("user1", bpf.getFrozenBy());
        assertNull(bpf.getConfirmedAt());
        assertNull(bpf.getConfirmedBy());
    }

    @Test
    void isFrozen_WhenFrozen_ReturnsTrue() {
        BranchPurchaseFrozen bpf = BranchPurchaseFrozen.createFrozen("BR001", LocalDate.now(), "user1");

        assertTrue(bpf.isFrozen());
        assertFalse(bpf.isConfirmed());
    }

    @Test
    void isConfirmed_WhenConfirmed_ReturnsTrue() {
        BranchPurchaseFrozen bpf = BranchPurchaseFrozen.createFrozen("BR001", LocalDate.now(), "user1");
        bpf.confirm("user2");

        assertTrue(bpf.isConfirmed());
        assertFalse(bpf.isFrozen());
    }

    @Test
    void confirm_WhenFrozen_TransitionsToConfirmed() {
        BranchPurchaseFrozen bpf = BranchPurchaseFrozen.createFrozen("BR001", LocalDate.now(), "user1");

        bpf.confirm("user2");

        assertEquals(FrozenStatus.CONFIRMED, bpf.getStatus());
        assertNotNull(bpf.getConfirmedAt());
        assertEquals("user2", bpf.getConfirmedBy());
    }

    @Test
    void confirm_WhenAlreadyConfirmed_ThrowsException() {
        BranchPurchaseFrozen bpf = BranchPurchaseFrozen.createFrozen("BR001", LocalDate.now(), "user1");
        bpf.confirm("user2");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> bpf.confirm("user3"));
        assertEquals("營業所已經確認", ex.getMessage());
    }

    @Test
    void assertCanUnfreeze_WhenFrozen_NoException() {
        BranchPurchaseFrozen bpf = BranchPurchaseFrozen.createFrozen("BR001", LocalDate.now(), "user1");

        assertDoesNotThrow(bpf::assertCanUnfreeze);
    }

    @Test
    void assertCanUnfreeze_WhenConfirmed_ThrowsException() {
        BranchPurchaseFrozen bpf = BranchPurchaseFrozen.createFrozen("BR001", LocalDate.now(), "user1");
        bpf.confirm("user2");

        IllegalStateException ex = assertThrows(IllegalStateException.class, bpf::assertCanUnfreeze);
        assertEquals("營業所已確認，無法解除凍結", ex.getMessage());
    }

    @Test
    void assertCanEditConfirmedQty_WhenFrozen_NoException() {
        BranchPurchaseFrozen bpf = BranchPurchaseFrozen.createFrozen("BR001", LocalDate.now(), "user1");

        assertDoesNotThrow(bpf::assertCanEditConfirmedQty);
    }

    @Test
    void assertCanEditConfirmedQty_WhenConfirmed_ThrowsException() {
        BranchPurchaseFrozen bpf = BranchPurchaseFrozen.createFrozen("BR001", LocalDate.now(), "user1");
        bpf.confirm("user2");

        IllegalStateException ex = assertThrows(IllegalStateException.class, bpf::assertCanEditConfirmedQty);
        assertEquals("營業所已確認，無法再調整確認數量", ex.getMessage());
    }

    @Test
    void assertCanAggregate_WhenConfirmed_NoException() {
        BranchPurchaseFrozen bpf = BranchPurchaseFrozen.createFrozen("BR001", LocalDate.now(), "user1");
        bpf.confirm("user2");

        assertDoesNotThrow(bpf::assertCanAggregate);
    }

    @Test
    void assertCanAggregate_WhenFrozen_ThrowsException() {
        BranchPurchaseFrozen bpf = BranchPurchaseFrozen.createFrozen("BR001", LocalDate.now(), "user1");

        IllegalStateException ex = assertThrows(IllegalStateException.class, bpf::assertCanAggregate);
        assertEquals("營業所尚未確認，無法彙總", ex.getMessage());
    }
}
