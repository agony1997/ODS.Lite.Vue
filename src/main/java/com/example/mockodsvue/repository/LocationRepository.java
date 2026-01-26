package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.branch.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

    Optional<Location> findByLocationCode(String locationCode);

    List<Location> findByBranchCodeAndStatus(String branchCode, String status);

    Optional<Location> findByUserId(String userId);
}
