package com.qurilo.order_service.dto;

import com.qurilo.order_service.entity.Order;
import com.qurilo.order_service.utils.UtilityMethods;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponse {

	private Long id;
	private Long userId;
	private String productId;
	private String productName;
	private Integer quantity;
	private BigDecimal unitPrice;
	private BigDecimal totalPrice;
	private String status;
	private String createdAt;
	private String updatedAt;

	public OrderResponse() {
	}

	public OrderResponse(Long id, Long userId, String productId, String productName, Integer quantity,
			BigDecimal unitPrice, BigDecimal totalPrice, String status, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		this.id = id;
		this.userId = userId;
		this.productId = productId;
		this.productName = productName;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.totalPrice = totalPrice;
		this.status = status;
		this.createdAt = UtilityMethods.dateFormater(createdAt);
		this.updatedAt = UtilityMethods.dateFormater(updatedAt);
	}

	public static OrderResponse fromOrder(Order order) {
		return new OrderResponse(order.getId(), order.getUserId(), order.getProductId(), order.getProductName(),
				order.getQuantity(), order.getUnitPrice(), order.getTotalPrice(), order.getStatus().name(),
				order.getCreatedAt(), order.getUpdatedAt());
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public String toString() {
		return "OrderResponse{" + "id=" + id + ", userId=" + userId + ", productId='" + productId + '\''
				+ ", productName='" + productName + '\'' + ", quantity=" + quantity + ", unitPrice=" + unitPrice
				+ ", totalPrice=" + totalPrice + ", status='" + status + '\'' + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + '}';
	}
}