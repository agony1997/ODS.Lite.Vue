package com.example.mockodsvue.master.repository;

import com.example.mockodsvue.master.model.entity.Factory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactoryRepository extends JpaRepository<Factory, String> {

    List<Factory> findByStatus(String status);
}
