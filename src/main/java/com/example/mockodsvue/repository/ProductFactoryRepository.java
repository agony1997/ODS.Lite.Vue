package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.master.ProductFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductFactoryRepository extends JpaRepository<ProductFactory, Integer> {

    List<ProductFactory> findByProductCode(String productCode);

    List<ProductFactory> findByFactoryCode(String factoryCode);

    Optional<ProductFactory> findByProductCodeAndFactoryCode(String productCode, String factoryCode);

    Optional<ProductFactory> findByProductCodeAndIsDefaultTrue(String productCode);
}
