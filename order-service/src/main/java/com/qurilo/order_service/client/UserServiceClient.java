package com.qurilo.order_service.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.qurilo.order_service.dto.CustomApiResponse;
import com.qurilo.order_service.dto.UserResponse;

@Component
public class UserServiceClient {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);

	@Autowired
	private RestTemplate restTemplate;

	private static final String USER_SERVICE_BASE_URL = "http://localhost:8082/api/v1";

	public CustomApiResponse<UserResponse> getUserById(Long userId) {
		try {
			String url = USER_SERVICE_BASE_URL + "/" + userId;
			logger.info("Verifying user from User Service: {}", url);

			ResponseEntity<CustomApiResponse<UserResponse>> response = restTemplate.exchange(url,
					org.springframework.http.HttpMethod.GET, null,
					new ParameterizedTypeReference<CustomApiResponse<UserResponse>>() {
					});

			if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
				CustomApiResponse<UserResponse> userResponse = response.getBody();
				if (userResponse.isSuccess()) {
					logger.info("User verification successful for user ID: {}", userId);
					return userResponse;
				} else {
					logger.warn("User verification failed for user ID: {} - {}", userId, userResponse.getMessage());
					return userResponse;
				}
			} else {
				logger.error("Failed to verify user: {}", userId);
				return new CustomApiResponse<>(false, "User verification failed", null, HttpStatus.NOT_FOUND);
			}

		} catch (HttpClientErrorException.NotFound e) {
			logger.error("User not found: {}", userId);
			return new CustomApiResponse<>(false, "User not found", null, HttpStatus.NOT_FOUND);
		} catch (HttpClientErrorException.BadRequest e) {
			logger.error("Bad request for user verification: {}", userId);
			return new CustomApiResponse<>(false, "Invalid user ID", null, HttpStatus.BAD_REQUEST);
		} catch (ResourceAccessException e) {
			logger.error("User Service unavailable: {}", e.getMessage());
			return new CustomApiResponse<>(false, "User Service unavailable", null, HttpStatus.SERVICE_UNAVAILABLE);
		} catch (Exception e) {
			logger.error("Error verifying user {}: {}", userId, e.getMessage(), e);
			return new CustomApiResponse<>(false, "Error verifying user", null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}