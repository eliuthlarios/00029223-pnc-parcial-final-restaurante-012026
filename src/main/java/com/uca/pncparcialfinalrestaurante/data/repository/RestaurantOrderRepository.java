package com.uca.pncparcialfinalrestaurante.data.repository;

import com.uca.pncparcialfinalrestaurante.data.entity.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long> {
    List<RestaurantOrder> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<RestaurantOrder> findByTableBranchIdOrderByCreatedAtDesc(Long branchId);
}
