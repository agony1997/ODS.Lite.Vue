package com.example.mockodsvue.master.repository;

import com.example.mockodsvue.master.model.entity.Product;
import com.example.mockodsvue.master.model.enums.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findByStatus(String status);

    List<Product> findByCategory(ProductCategory category);

    List<Product> findByCategoryAndStatus(ProductCategory category, String status);
}
