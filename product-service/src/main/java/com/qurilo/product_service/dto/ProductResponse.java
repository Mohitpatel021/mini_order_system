package com.qurilo.product_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.qurilo.product_service.utils.UtilityMethods;

public class ProductResponse {
	private String id;
	private String productName;
	private String productDescription;
	private BigDecimal productPrice;
	private Integer productStockQuantity;
	private String productCategory;
	private String createdAt;
	private String updatedAt;

	public ProductResponse() {
	}

	public ProductResponse(String id, String productName, String productDescription, BigDecimal productPrice,
			Integer productStockQuantity, String productCategory) {
		this.id = id;
		this.productName = productName;
		this.productDescription = productDescription;
		this.productPrice = productPrice;
		this.productStockQuantity = productStockQuantity;
		this.productCategory = productCategory;
		this.createdAt = UtilityMethods.dateFormater(LocalDateTime.now());
		this.updatedAt = UtilityMethods.dateFormater(LocalDateTime.now());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public BigDecimal getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(BigDecimal productPrice) {
		this.productPrice = productPrice;
	}

	public Integer getProductStockQuantity() {
		return productStockQuantity;
	}

	public void setProductStockQuantity(Integer productStockQuantity) {
		this.productStockQuantity = productStockQuantity;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = UtilityMethods.dateFormater(createdAt);
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = UtilityMethods.dateFormater(updatedAt);
	}

	public static ProductResponse fromProduct(com.qurilo.product_service.entity.Product product) {
		return new ProductResponse(product.getId(), product.getProductName(), product.getProductDescription(),
				product.getProductPrice(), product.getProductStockQuantity(), product.getProductCategory());
	}

	@Override
	public String toString() {
		return "ProductResponse{" + "id='" + id + '\'' + ", productName='" + productName + '\''
				+ ", productDescription='" + productDescription + '\'' + ", productPrice=" + productPrice
				+ ", productStockQuantity=" + productStockQuantity + ", productCategory='" + productCategory + '\''
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
	}
}