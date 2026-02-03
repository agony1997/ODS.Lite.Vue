package com.example.mockodsvue.branch.repository;

import com.example.mockodsvue.branch.model.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

    Optional<Location> findByLocationCode(String locationCode);

    List<Location> findByBranchCodeAndStatus(String branchCode, String status);

    Optional<Location> findByUserCode(String userCode);

    /**
     * 依營業所代碼查詢所有儲位
     */
    List<Location> findByBranchCode(String branchCode);
}
