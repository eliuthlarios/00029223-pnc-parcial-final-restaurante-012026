package com.uca.pncparcialfinalrestaurante.data.repository;

import com.uca.pncparcialfinalrestaurante.data.entity.RestaurantBranch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantBranchRepository extends JpaRepository<RestaurantBranch, Long> {
    boolean existsByNameIgnoreCase(String name);
}
