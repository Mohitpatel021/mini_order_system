package com.qurilo.product_service.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.ResourceAccessException;

import com.qurilo.product_service.dto.CustomApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(InvalidInputException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidInputException(InvalidInputException ex) {
		Map<String, Object> response = new HashMap<>();
		response.put("success", false);
		response.put("message", ex.getMessage());
		response.put("status", HttpStatus.BAD_REQUEST.value());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleProductNotFoundException(ProductNotFoundException ex) {
		Map<String, Object> response = new HashMap<>();
		response.put("success", false);
		response.put("message", ex.getMessage());
		response.put("status", HttpStatus.NOT_FOUND.value());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(ProductServiceException.class)
	public ResponseEntity<Map<String, Object>> handleProductServiceException(ProductServiceException ex) {
		Map<String, Object> response = new HashMap<>();
		response.put("success", false);
		response.put("message", ex.getMessage());
		response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	@ExceptionHandler(InternalServerError.class)
	public ResponseEntity<Map<String, Object>> handleUserAlreadyExistsException(InternalServerError ex) {
		Map<String, Object> response = new HashMap<>();
		response.put("success", false);
		response.put("message", ex.getMessage());
		response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CustomApiResponse<Map<String, String>>> handleValidationExceptions(
			MethodArgumentNotValidException ex) {

		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		logger.warn("Validation error: {}", errors);
		CustomApiResponse<Map<String, String>> response = CustomApiResponse.error("Validation failed",
				HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResourceAccessException.class)
	public ResponseEntity<CustomApiResponse<String>> handleResourceAccessException(ResourceAccessException ex) {
		logger.error("Service unavailable: {}", ex.getMessage());
		CustomApiResponse<String> response = CustomApiResponse.error("Service temporarily unavailable",
				HttpStatus.SERVICE_UNAVAILABLE);
		return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<CustomApiResponse<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
		logger.warn("Illegal argument: {}", ex.getMessage());
		CustomApiResponse<String> response = CustomApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<CustomApiResponse<String>> handleGenericException(Exception ex) {
		logger.error("Unexpected error: {}", ex.getMessage(), ex);
		CustomApiResponse<String> response = CustomApiResponse.error("Internal server error",
				HttpStatus.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}