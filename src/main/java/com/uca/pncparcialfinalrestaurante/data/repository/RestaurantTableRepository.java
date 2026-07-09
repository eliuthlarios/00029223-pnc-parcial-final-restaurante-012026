package com.uca.pncparcialfinalrestaurante.data.repository;

import com.uca.pncparcialfinalrestaurante.data.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    List<RestaurantTable> findByBranchId(Long branchId);
    boolean existsByBranchIdAndCodeIgnoreCase(Long branchId, String code);
}
