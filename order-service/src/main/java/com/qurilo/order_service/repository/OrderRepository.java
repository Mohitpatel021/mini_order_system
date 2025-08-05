package com.qurilo.order_service.repository;

import com.qurilo.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT o FROM Order o WHERE " +
           "CAST(o.userId AS string) LIKE %:searchTerm% OR " +
           "o.productId LIKE %:searchTerm% OR " +
           "o.status LIKE %:searchTerm%")
    List<Order> searchOrders(@Param("searchTerm") String searchTerm);
} 