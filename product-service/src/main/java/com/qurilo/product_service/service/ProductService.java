package com.qurilo.product_service.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.qurilo.product_service.dto.CustomApiResponse;
import com.qurilo.product_service.dto.ProductRequest;
import com.qurilo.product_service.dto.ProductResponse;
import com.qurilo.product_service.entity.Product;
import com.qurilo.product_service.exceptions.InvalidInputException;
import com.qurilo.product_service.exceptions.ProductNotFoundException;
import com.qurilo.product_service.exceptions.ProductServiceException;
import com.qurilo.product_service.repository.ProductRepository;

@Service
@Transactional
public class ProductService {

	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

	@Autowired
	private ProductRepository productRepository;

	@CacheEvict(value = "products", allEntries = true)
	public CustomApiResponse<ProductResponse> addProduct(ProductRequest request) {
		try {

			if (productRepository.existsByProductName(request.getProductName())) {
				return CustomApiResponse
						.conflict("Product with name '" + request.getProductName() + "' already exists");
			}

			Product product = new Product();
			product.setProductName(request.getProductName());
			product.setProductDescription(request.getProductDescription());
			product.setProductPrice(request.getProductPrice());
			product.setProductStockQuantity(request.getProductStockQuantity());
			product.setProductCategory(request.getProductCategory());

			Product savedProduct = productRepository.save(product);

			ProductResponse response = ProductResponse.fromProduct(savedProduct);
			return CustomApiResponse.created("Product added successfully", response);

		} catch (Exception e) {
			return CustomApiResponse.internalError("Failed to add product: " + e.getMessage());
		}
	}

	@Cacheable(value = "products", key = "#productId", unless = "#result == null")
	public ProductResponse getProductById(String productId) {
		if (!StringUtils.hasText(productId)) {
			logger.warn("Invalid product ID provided: {}", productId);
			throw new InvalidInputException("Product ID must not be null or empty");
		}

		final String trimmedProductId = productId.trim();
		logger.debug("Fetching product with ID: {}", trimmedProductId);

		try {
			return productRepository.findById(trimmedProductId).map(product -> {
				logger.debug("Product found with ID: {}", trimmedProductId);
				return ProductResponse.fromProduct(product);
			}).orElseThrow(() -> {
				logger.warn("Product not found with ID: {}", trimmedProductId);
				return new ProductNotFoundException("Product not found with ID: " + trimmedProductId);
			});

		} catch (InvalidInputException | ProductNotFoundException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Unexpected error occurred while fetching product with ID: {}", trimmedProductId, e);
			throw new ProductServiceException("Failed to retrieve product with ID: " + trimmedProductId, e);
		}
	}

	public Boolean checkProductStock(String productId, Integer requiredQuantity) {
		if (!StringUtils.hasText(productId)) {
			logger.warn("Invalid product ID provided: {}", productId);
			throw new InvalidInputException("Product ID must not be null or empty");
		}

		if (requiredQuantity == null || requiredQuantity <= 0) {
			logger.warn("Invalid required quantity provided: {}", requiredQuantity);
			throw new InvalidInputException("Required quantity must be greater than 0");
		}

		final String trimmedProductId = productId.trim();
		logger.debug("Checking stock for product ID: {} with required quantity: {}", trimmedProductId, requiredQuantity);

		try {
			return productRepository.findById(trimmedProductId).map(product -> {
				boolean hasStock = product.getProductStockQuantity() >= requiredQuantity;
				logger.debug("Product {} has stock: {}, required: {}, result: {}", 
					trimmedProductId, product.getProductStockQuantity(), requiredQuantity, hasStock);
				return hasStock;
			}).orElse(false);

		} catch (Exception e) {
			logger.error("Error checking stock for product ID: {}", trimmedProductId, e);
			throw new ProductServiceException("Failed to check stock for product ID: " + trimmedProductId, e);
		}
	}

	@Cacheable(value = "products", key = "'all'")
	public CustomApiResponse<List<ProductResponse>> getAllProducts() {
		try {
			List<Product> products = productRepository.findAll();
			if (products.isEmpty()) {
				return CustomApiResponse.ok("No products found", List.of());
			}
			List<ProductResponse> responses = products.stream().map(ProductResponse::fromProduct).toList();
			return CustomApiResponse.ok("Products retrieved successfully", responses);
		} catch (Exception e) {
			return new CustomApiResponse<>(false, "Failed to get all products: " + e.getMessage(), List.of(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Cacheable(value = "products", key = "#category")
	public CustomApiResponse<List<ProductResponse>> getProductsByCategory(String category) {
		try {
			if (category == null || category.trim().isEmpty()) {
				return new CustomApiResponse<>(false, "Category cannot be null or empty", List.of(),
						HttpStatus.BAD_REQUEST);
			}

			List<Product> products = productRepository.findByProductCategory(category.trim());
			if (products.isEmpty()) {
				return CustomApiResponse.ok("No products found in category: " + category, List.of());
			}
			List<ProductResponse> responses = products.stream().map(ProductResponse::fromProduct).toList();
			return CustomApiResponse.ok("Products retrieved successfully", responses);
		} catch (Exception e) {
			return new CustomApiResponse<>(false, "Failed to get products by category: " + e.getMessage(), List.of(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Cacheable(value = "products", key = "#name")
	public CustomApiResponse<List<ProductResponse>> searchProductsByName(String name) {
		try {
			if (name == null || name.trim().isEmpty()) {
				return new CustomApiResponse<>(false, "Product name cannot be null or empty", List.of(),
						HttpStatus.BAD_REQUEST);
			}

			List<Product> products = productRepository.findByProductNameContainingIgnoreCase(name.trim());
			if (products.isEmpty()) {
				return CustomApiResponse.ok("No products found with name: " + name, List.of());
			}
			List<ProductResponse> responses = products.stream().map(ProductResponse::fromProduct).toList();
			return CustomApiResponse.ok("Products retrieved successfully", responses);
		} catch (Exception e) {
			return new CustomApiResponse<>(false, "Failed to search products by name: " + e.getMessage(), List.of(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Cacheable(value = "products", key = "'inStock'")
	public CustomApiResponse<List<ProductResponse>> getProductsInStock() {
		try {
			List<Product> products = productRepository.findByProductStockQuantityGreaterThan(0);
			if (products.isEmpty()) {
				return CustomApiResponse.ok("No products in stock", List.of());
			}
			List<ProductResponse> responses = products.stream().map(ProductResponse::fromProduct).toList();
			return CustomApiResponse.ok("Products in stock retrieved successfully", responses);
		} catch (Exception e) {
			return new CustomApiResponse<>(false, "Failed to get products in stock: " + e.getMessage(), List.of(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@CacheEvict(value = "products", allEntries = true)
	public CustomApiResponse<ProductResponse> updateProductStock(String productId, Integer newStockQuantity) {
		try {
			if (productId == null || productId.trim().isEmpty()) {
				return CustomApiResponse.badRequest("Product ID cannot be null or empty");
			}

			if (newStockQuantity == null || newStockQuantity < 0) {
				return CustomApiResponse.badRequest("Stock quantity cannot be null or negative");
			}

			Optional<Product> productOpt = productRepository.findById(productId.trim());
			if (productOpt.isPresent()) {
				Product product = productOpt.get();
				product.setProductStockQuantity(newStockQuantity);
				Product updatedProduct = productRepository.save(product);
				ProductResponse response = ProductResponse.fromProduct(updatedProduct);
				return CustomApiResponse.ok("Product stock updated successfully", response);
			} else {
				return CustomApiResponse.notFound("Product not found with ID: " + productId);
			}
		} catch (Exception e) {
			return CustomApiResponse.internalError("Failed to update product stock: " + e.getMessage());
		}
	}
}