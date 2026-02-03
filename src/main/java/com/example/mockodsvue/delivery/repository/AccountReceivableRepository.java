package com.example.mockodsvue.delivery.repository;

import com.example.mockodsvue.delivery.model.entity.AccountReceivable;
import com.example.mockodsvue.delivery.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountReceivableRepository extends JpaRepository<AccountReceivable, Integer> {

    Optional<AccountReceivable> findByArNo(String arNo);

    Optional<AccountReceivable> findByDeliveryNo(String deliveryNo);

    List<AccountReceivable> findByBranchCode(String branchCode);

    List<AccountReceivable> findByBranchCodeAndStatus(String branchCode, PaymentStatus status);

    List<AccountReceivable> findByCustomerCode(String customerCode);

    List<AccountReceivable> findByCustomerCodeAndStatus(String customerCode, PaymentStatus status);

    List<AccountReceivable> findByDueDateBeforeAndStatusNot(LocalDate dueDate, PaymentStatus status);
}
