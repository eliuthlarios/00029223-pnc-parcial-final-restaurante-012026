package com.uca.pncparcialfinalrestaurante.data.repository;

import com.uca.pncparcialfinalrestaurante.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();
    boolean existsByNameIgnoreCase(String name);
}
