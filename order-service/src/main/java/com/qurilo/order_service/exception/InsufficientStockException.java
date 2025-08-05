package com.qurilo.order_service.exception;

public class InsufficientStockException extends RuntimeException {
    
    public InsufficientStockException(String message) {
        super(message);
    }
    
    public InsufficientStockException(String productId, Integer available, Integer required) {
        super(String.format("Insufficient stock for product %s. Available: %d, Required: %d", 
                           productId, available, required));
    }
} 