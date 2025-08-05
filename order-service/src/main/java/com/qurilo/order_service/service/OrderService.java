package com.qurilo.order_service.service;

import com.qurilo.order_service.client.ProductServiceClient;
import com.qurilo.order_service.client.UserServiceClient;
import com.qurilo.order_service.dto.CustomApiResponse;
import com.qurilo.order_service.dto.OrderRequest;
import com.qurilo.order_service.dto.OrderResponse;
import com.qurilo.order_service.dto.ProductResponse;
import com.qurilo.order_service.dto.UserResponse;
import com.qurilo.order_service.entity.Order;
import com.qurilo.order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ProductServiceClient productServiceClient;

	@Autowired
	private UserServiceClient userServiceClient;

	@Transactional
	public CustomApiResponse<OrderResponse> processOrder(OrderRequest request) {
		try {
			logger.info("Processing order for user: {} and product: {} with quantity: {}", request.getUserId(),
					request.getProductId(), request.getQuantity());

			CustomApiResponse<UserResponse> userVerificationResponse = userServiceClient
					.getUserById(request.getUserId());

			if (!userVerificationResponse.isSuccess()) {
				logger.warn("User verification failed for user ID: {} - {}", request.getUserId(),
						userVerificationResponse.getMessage());
				return CustomApiResponse.error("Invalid user: " + userVerificationResponse.getMessage(),
						userVerificationResponse.getStatus());
			}

			logger.info("User verification successful for user ID: {}", request.getUserId());

			CustomApiResponse<ProductResponse> productResponse = productServiceClient
					.getProductById(request.getProductId());

			if (!productResponse.isSuccess() || productResponse.getData() == null) {
				logger.warn("Product not found: {}", request.getProductId());
				return CustomApiResponse.error("Product not found", HttpStatus.NOT_FOUND);
			}

			ProductResponse product = productResponse.getData();

			if (product.getProductStockQuantity() < request.getQuantity()) {
				logger.warn("Insufficient stock for product: {}. Available: {}, Required: {}", request.getProductId(),
						product.getProductStockQuantity(), request.getQuantity());
				return CustomApiResponse.error("Insufficient Stock", HttpStatus.BAD_REQUEST);
			}

			Order order = new Order(request.getUserId(), request.getProductId(), product.getProductName(),
					request.getQuantity(), product.getProductPrice(), Order.OrderStatus.CONFIRMED);
			Order savedOrder = orderRepository.save(order);
			logger.info("Order created successfully with ID: {}", savedOrder.getId());
			Integer newStockQuantity = product.getProductStockQuantity() - request.getQuantity();
			CustomApiResponse<ProductResponse> stockUpdateResponse = productServiceClient
					.updateProductStock(request.getProductId(), newStockQuantity);
			if (!stockUpdateResponse.isSuccess()) {
				logger.error("Failed to update product stock for order: {}", savedOrder.getId());
			} else {
				logger.info("Product stock updated successfully for order: {}", savedOrder.getId());
			}
			clearProductCache(request.getProductId());
			OrderResponse orderResponse = OrderResponse.fromOrder(savedOrder);
			return new CustomApiResponse<>(true, "Order processed successfully", orderResponse, HttpStatus.CREATED);

		} catch (Exception e) {
			logger.error("Error processing order: {}", e.getMessage(), e);
			return CustomApiResponse.error("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public CustomApiResponse<List<OrderResponse>> getOrders(String searchTerm) {
		try {
			List<Order> orders;

			if (searchTerm == null || searchTerm.trim().isEmpty()) {
				orders = orderRepository.findAll();
			} else {
				orders = orderRepository.searchOrders(searchTerm.trim());
			}

			List<OrderResponse> orderResponses = orders.stream().map(OrderResponse::fromOrder)
					.collect(Collectors.toList());

			return new CustomApiResponse<>(true, "Orders retrieved successfully", orderResponses, HttpStatus.OK);

		} catch (Exception e) {
			logger.error("Error retrieving orders: {}", e.getMessage(), e);
			return CustomApiResponse.error("Error retrieving orders", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@CacheEvict(value = "products", key = "#productId")
	public void clearProductCache(String productId) {
		logger.info("Clearing product cache for: {}", productId);
	}
}