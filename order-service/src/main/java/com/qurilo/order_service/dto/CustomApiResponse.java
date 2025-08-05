package com.qurilo.order_service.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.qurilo.order_service.utils.UtilityMethods;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomApiResponse<T> {

	private boolean success;
	private String message;
	private T data;
	@JsonIgnore
	private HttpStatus status;
	private String timestamp;

	public CustomApiResponse() {
	}

	public CustomApiResponse(boolean success, String message, T data, HttpStatus status) {
		this.success = success;
		this.message = message;
		this.data = data;
		this.status = status;
		this.timestamp = UtilityMethods.dateFormater(LocalDateTime.now());
	}

	public static <T> CustomApiResponse<T> ok(String message, T data) {
		return new CustomApiResponse<>(true, message, data, HttpStatus.OK);
	}

	public static <T> CustomApiResponse<T> created(String message, T data) {
		return new CustomApiResponse<>(true, message, data, HttpStatus.CREATED);
	}

	public static <T> CustomApiResponse<T> error(String message, HttpStatus status) {
		return new CustomApiResponse<>(false, message, null, status);
	}

	public static CustomApiResponse<ProductResponse> conflict(String message) {
		return new CustomApiResponse<>(false, message, null, HttpStatus.CONFLICT);
	}

	public static CustomApiResponse<ProductResponse> internalError(String message) {
		return new CustomApiResponse<>(false, message, null, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public static CustomApiResponse<ProductResponse> badRequest(String message) {
		return new CustomApiResponse<>(false, message, null, HttpStatus.BAD_REQUEST);
	}

	public static CustomApiResponse<ProductResponse> notFound(String message) {
		return new CustomApiResponse<>(false, message, null, HttpStatus.NOT_FOUND);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
