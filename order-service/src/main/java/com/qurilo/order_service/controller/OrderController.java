package com.qurilo.order_service.controller;

import com.qurilo.order_service.dto.CustomApiResponse;
import com.qurilo.order_service.dto.OrderRequest;
import com.qurilo.order_service.dto.OrderResponse;
import com.qurilo.order_service.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<CustomApiResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest request) {
        CustomApiResponse<OrderResponse> response = orderService.processOrder(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping
    public ResponseEntity<CustomApiResponse<List<OrderResponse>>> getOrders(
            @RequestParam(required = false) String search) {
        CustomApiResponse<List<OrderResponse>> response = orderService.getOrders(search);
        return new ResponseEntity<>(response, response.getStatus());
    }
} 