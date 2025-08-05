package com.qurilo.order_service.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.qurilo.order_service.dto.CustomApiResponse;
import com.qurilo.order_service.dto.ProductResponse;

@Component
public class ProductServiceClient {

	private static final Logger logger = LoggerFactory.getLogger(ProductServiceClient.class);

	@Autowired
	private RestTemplate restTemplate;

	private static final String PRODUCT_SERVICE_BASE_URL = "http://localhost:8080/api/v1/products";

	@Cacheable(value = "products", key = "#productId", unless = "#result == null || !#result.isSuccess()")
	public CustomApiResponse<ProductResponse> getProductById(String productId) {
		try {
			String url = PRODUCT_SERVICE_BASE_URL + "/" + productId;
			logger.info("Fetching product from Product Service: {}", url);

			ResponseEntity<ProductResponse> response = restTemplate.getForEntity(url, ProductResponse.class);

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				logger.info("Successfully retrieved product: {}", productId);
				logger.debug("Product response body: {}", response.getBody());
				return new CustomApiResponse<ProductResponse>(true, "Product retrieved successfully",
						response.getBody(), HttpStatus.OK);
			} else {
				logger.error("Failed to retrieve product: {}", productId);
				return new CustomApiResponse<ProductResponse>(false, "Product not found", null, HttpStatus.NOT_FOUND);
			}

		} catch (HttpClientErrorException.NotFound e) {
			logger.error("Product not found: {}", productId);
			return new CustomApiResponse<ProductResponse>(false, "Product not found", null, HttpStatus.NOT_FOUND);
		} catch (HttpClientErrorException.BadRequest e) {
			logger.error("Bad request for product: {}", productId);
			return new CustomApiResponse<ProductResponse>(false, "Invalid product ID", null, HttpStatus.BAD_REQUEST);
		} catch (ResourceAccessException e) {
			logger.error("Product Service unavailable: {}", e.getMessage());
			return new CustomApiResponse<ProductResponse>(false, "Product Service unavailable", null,
					HttpStatus.SERVICE_UNAVAILABLE);
		} catch (Exception e) {
			logger.error("Error fetching product {}: {}", productId, e.getMessage(), e);
			return new CustomApiResponse<ProductResponse>(false, "Error fetching product", null,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public Boolean checkProductStock(String productId, Integer requiredQuantity) {
		try {
			String url = PRODUCT_SERVICE_BASE_URL + "/" + productId + "/check-stock?requiredQuantity=" + requiredQuantity;
			logger.info("Checking stock for product: {} with required quantity: {}", productId, requiredQuantity);

			ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				logger.info("Stock check completed for product: {}, result: {}", productId, response.getBody());
				return response.getBody();
			} else {
				logger.error("Failed to check stock for product: {}", productId);
				return false;
			}

		} catch (HttpClientErrorException.NotFound e) {
			logger.error("Product not found for stock check: {}", productId);
			return false;
		} catch (HttpClientErrorException.BadRequest e) {
			logger.error("Bad request for stock check: {}", productId);
			return false;
		} catch (ResourceAccessException e) {
			logger.error("Product Service unavailable for stock check: {}", e.getMessage());
			return false;
		} catch (Exception e) {
			logger.error("Error checking stock for product {}: {}", productId, e.getMessage());
			return false;
		}
	}

	public CustomApiResponse<ProductResponse> updateProductStock(String productId, Integer newStockQuantity) {
		try {
			String url = PRODUCT_SERVICE_BASE_URL + "/" + productId + "/stock?stockQuantity=" + newStockQuantity;
			logger.info("Updating product stock: {} to quantity: {}", productId, newStockQuantity);

			ResponseEntity<CustomApiResponse<ProductResponse>> response = restTemplate.exchange(url,
					org.springframework.http.HttpMethod.PUT, null,
					new ParameterizedTypeReference<CustomApiResponse<ProductResponse>>() {
					});

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				logger.info("Successfully updated product stock: {}", productId);
				return response.getBody();
			} else {
				logger.error("Failed to update product stock: {}", productId);
				return CustomApiResponse.error("Failed to update product stock", HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} catch (HttpClientErrorException.NotFound e) {
			logger.error("Product not found for stock update: {}", productId);
			return CustomApiResponse.error("Product not found", HttpStatus.NOT_FOUND);
		} catch (HttpClientErrorException.BadRequest e) {
			logger.error("Bad request for stock update: {}", productId);
			return CustomApiResponse.error("Invalid stock quantity", HttpStatus.BAD_REQUEST);
		} catch (ResourceAccessException e) {
			logger.error("Product Service unavailable for stock update: {}", e.getMessage());
			return CustomApiResponse.error("Product Service unavailable", HttpStatus.SERVICE_UNAVAILABLE);
		} catch (Exception e) {
			logger.error("Error updating product stock {}: {}", productId, e.getMessage());
			return CustomApiResponse.error("Error updating product stock", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}