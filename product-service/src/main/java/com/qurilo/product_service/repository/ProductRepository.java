package com.qurilo.product_service.repository;

import com.qurilo.product_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    
    Optional<Product> findById(String productId);
    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findAll();

    @Query("SELECT p FROM Product p WHERE LOWER(p.productCategory) = LOWER(:category) ORDER BY p.productName ASC")
    List<Product> findByProductCategory(@Param("category") String category);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY p.productName ASC")
    List<Product> findByProductNameContainingIgnoreCase(@Param("name") String name);
    
    @Query("SELECT p FROM Product p WHERE p.productStockQuantity > :stockQuantity ORDER BY p.productStockQuantity DESC")
    List<Product> findByProductStockQuantityGreaterThan(@Param("stockQuantity") Integer stockQuantity);
    
    boolean existsByProductName(String productName);
} 