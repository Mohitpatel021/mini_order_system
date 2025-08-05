package com.qurilo.order_service.dto;

import java.math.BigDecimal;

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
			Integer productStockQuantity, String productCategory, String createdAt, String updatedAt) {
		this.id = id;
		this.productName = productName;
		this.productDescription = productDescription;
		this.productPrice = productPrice;
		this.productStockQuantity = productStockQuantity;
		this.productCategory = productCategory;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
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
		return "ProductResponse{" + "id='" + id + '\'' + ", productName='" + productName + '\''
				+ ", productDescription='" + productDescription + '\'' + ", productPrice=" + productPrice
				+ ", productStockQuantity=" + productStockQuantity + ", productCategory='" + productCategory + '\''
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + '}';
	}
}