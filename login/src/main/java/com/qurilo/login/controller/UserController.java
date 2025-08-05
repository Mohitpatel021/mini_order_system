package com.qurilo.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qurilo.login.dto.CustomApiResponse;
import com.qurilo.login.dto.UserRegistrationRequest;
import com.qurilo.login.entity.Users;
import com.qurilo.login.responses.UserResponse;
import com.qurilo.login.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "User Management", description = "APIs for user registration and management")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public ResponseEntity<?> welcome() {
		return new ResponseEntity<>(new String("Welcome to the login Service"), HttpStatus.OK);
	}

	@PostMapping("/register")
	@Operation(summary = "Register a new user", description = "Creates a new user account with the provided information. Validates username and email uniqueness.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRegistrationRequest.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(name = "Success Response", value = """
					{
					   "id": 1,
					    "username": "xyz-usere",
					    "email": "xyz@gmail.com",
					    "firstName": "abc",
					    "lastName": "xyz",
					    "createdAt": "2024-01-01T10:00:00",
					    "updatedAt": "2024-01-01T10:00:00"
					}
					"""))),
			@ApiResponse(responseCode = "400", description = "Validation error - Invalid input data"),
			@ApiResponse(responseCode = "409", description = "Conflict - Username or email already exists"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<CustomApiResponse<?>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
		CustomApiResponse<?> response = userService.registerUser(request);
		return new ResponseEntity<>(response, response.getStatus());
	}

	@GetMapping("/user")
	@Operation(summary = "Search user by username or email", description = "Searches for a user using either username or email. Returns user details if found.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Users.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(name = "Success Response", value = """
					{
					   "id": 1,
					    "username": "xyz-usere",
					    "email": "xyz@gmail.com",
					    "firstName": "abc",
					    "lastName": "xyz",
					    "createdAt": "2024-01-01T10:00:00",
					    "updatedAt": "2024-01-01T10:00:00"
					}
					"""))),
			@ApiResponse(responseCode = "400", description = "Bad request - Invalid input parameter"),
			@ApiResponse(responseCode = "404", description = "User not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<CustomApiResponse<UserResponse>> searchUser(
			@RequestParam(name = "usernameOrEmail", required = true) String usernameOrEmail) {
		CustomApiResponse<UserResponse> response = userService.getUserDetails(usernameOrEmail);
		return new ResponseEntity<>(response, response.getStatus());
	}

	@GetMapping("/{userId}")
	@Operation(summary = "Get user by ID", description = "Retrieves user details using the provided user ID. Returns user information if found.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User found successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Users.class), examples = @io.swagger.v3.oas.annotations.media.ExampleObject(name = "Success Response", value = """
					{
					    "id": 1,
					    "username": "xyz-usere",
					    "email": "xyz@gmail.com",
					    "firstName": "abc",
					    "lastName": "xyz",
					    "createdAt": "2024-01-01T10:00:00",
					    "updatedAt": "2024-01-01T10:00:00"
					}
					"""))),
			@ApiResponse(responseCode = "400", description = "Bad request - Invalid user ID"),
			@ApiResponse(responseCode = "404", description = "User not found"),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	public ResponseEntity<CustomApiResponse<?>> getUserById(
			@PathVariable(name = "userId", required = true) Long userId) {
		CustomApiResponse<?> response = userService.getUserById(userId);
		return new ResponseEntity<>(response, response.getStatus());
	}

}
