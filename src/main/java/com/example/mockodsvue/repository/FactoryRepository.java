package com.example.mockodsvue.repository;

import com.example.mockodsvue.model.entity.master.Factory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactoryRepository extends JpaRepository<Factory, String> {

    List<Factory> findByStatus(String status);
}
