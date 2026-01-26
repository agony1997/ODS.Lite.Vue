package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.master.Product;
import com.example.mockodsvue.model.enums.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByStatus(String status);

    List<Product> findByCategory(ProductCategory category);

    List<Product> findByCategoryAndStatus(ProductCategory category, String status);
}
