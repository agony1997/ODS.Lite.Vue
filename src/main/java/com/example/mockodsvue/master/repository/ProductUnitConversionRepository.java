package com.example.mockodsvue.master.repository;

import com.example.mockodsvue.master.model.entity.ProductUnitConversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductUnitConversionRepository extends JpaRepository<ProductUnitConversion, Integer> {

    List<ProductUnitConversion> findByProductCode(String productCode);

    Optional<ProductUnitConversion> findByProductCodeAndFromUnitAndToUnit(String productCode, String fromUnit, String toUnit);
}
