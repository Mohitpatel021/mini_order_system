package com.qurilo.product_service.controller;

import com.qurilo.product_service.dto.CustomApiResponse;
import com.qurilo.product_service.dto.ProductRequest;
import com.qurilo.product_service.dto.ProductResponse;
import com.qurilo.product_service.entity.Product;
import com.qurilo.product_service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product Management", description = "APIs for product catalog management")
public class ProductController {

	@Autowired
	private ProductService productService;

	@PostMapping
	@Operation(summary = "Add new product", description = "Adds a new product to the catalog with name, price, and stock quantity")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Product created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductRequest.class))),
			@ApiResponse(responseCode = "400", description = "Bad request - Invalid input data"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<CustomApiResponse<ProductResponse>> addProduct(@Valid @RequestBody ProductRequest request) {
		CustomApiResponse<ProductResponse> response = productService.addProduct(request);
		return new ResponseEntity<>(response, response.getStatus());
	}

	@GetMapping("/{productId}")
	@Operation(summary = "Get product by ID", description = "Retrieves a specific product using its ID number")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Product found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
			@ApiResponse(responseCode = "400", description = "Bad request - Invalid product ID"),
			@ApiResponse(responseCode = "404", description = "Product not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<ProductResponse> getProductById(@PathVariable String productId) {
		ProductResponse product = productService.getProductById(productId);
		return ResponseEntity.ok(product);
	}

	@GetMapping
	@Operation(summary = "Get all products", description = "Retrieves all available products in the system")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Products retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class))),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<CustomApiResponse<List<ProductResponse>>> getAllProducts() {
		CustomApiResponse<List<ProductResponse>> response = productService.getAllProducts();
		return new ResponseEntity<>(response, response.getStatus());
	}

	@GetMapping("/category/{category}")
	@Operation(summary = "Get products by category", description = "Retrieves all products in a specific category")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
			@ApiResponse(responseCode = "400", description = "Bad request - Invalid category") })
	public ResponseEntity<CustomApiResponse<List<ProductResponse>>> getProductsByCategory(
			@PathVariable String category) {
		CustomApiResponse<List<ProductResponse>> response = productService.getProductsByCategory(category);
		return new ResponseEntity<>(response, response.getStatus());
	}

	@GetMapping("/search")
	@Operation(summary = "Search products by name", description = "Searches for products by name (case-insensitive)")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
			@ApiResponse(responseCode = "400", description = "Bad request - Invalid search term") })
	public ResponseEntity<CustomApiResponse<List<ProductResponse>>> searchProductsByName(@RequestParam String name) {
		CustomApiResponse<List<ProductResponse>> response = productService.searchProductsByName(name);
		return new ResponseEntity<>(response, response.getStatus());
	}

	@GetMapping("/in-stock")
	@Operation(summary = "Get products in stock", description = "Retrieves all products that have stock quantity greater than 0")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Products retrieved successfully") })
	public ResponseEntity<CustomApiResponse<List<ProductResponse>>> getProductsInStock() {
		CustomApiResponse<List<ProductResponse>> response = productService.getProductsInStock();
		return new ResponseEntity<>(response, response.getStatus());
	}

	@GetMapping("/{productId}/check-stock")
	@Operation(summary = "Check product stock availability", description = "Checks if product exists and has sufficient stock")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Stock check completed"),
			@ApiResponse(responseCode = "400", description = "Bad request - Invalid input"),
			@ApiResponse(responseCode = "404", description = "Product not found") })
	public ResponseEntity<Boolean> checkProductStock(@PathVariable String productId,
			@RequestParam Integer requiredQuantity) {
		Boolean hasStock = productService.checkProductStock(productId, requiredQuantity);
		return ResponseEntity.ok(hasStock);
	}

	@PutMapping("/{productId}/stock")
	@Operation(summary = "Update product stock", description = "Updates the stock quantity of a specific product")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Product stock updated successfully"),
			@ApiResponse(responseCode = "400", description = "Bad request - Invalid input"),
			@ApiResponse(responseCode = "404", description = "Product not found") })
	public ResponseEntity<CustomApiResponse<ProductResponse>> updateProductStock(@PathVariable String productId,
			@RequestParam Integer stockQuantity) {
		CustomApiResponse<ProductResponse> response = productService.updateProductStock(productId, stockQuantity);
		return new ResponseEntity<>(response, response.getStatus());
	}
}