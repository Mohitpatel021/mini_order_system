package com.qurilo.login.services;

import com.qurilo.login.dto.CustomApiResponse;
import com.qurilo.login.dto.UserRegistrationRequest;
import com.qurilo.login.entity.Users;
import com.qurilo.login.exception.InvalidUserIdException;
import com.qurilo.login.exception.UserAlreadyExistsException;
import com.qurilo.login.exception.UserNotFoundException;
import com.qurilo.login.repository.UsersRepository;
import com.qurilo.login.responses.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
	@Autowired
	private UsersRepository usersRepository;

	@Transactional
	@CacheEvict(value = "users", allEntries = true, condition = "#result.success")
	public CustomApiResponse<?> registerUser(UserRegistrationRequest request) {

		if (usersRepository.existsByUsername(request.getUsername())
				|| usersRepository.existsByEmail(request.getEmail())) {
			throw new UserAlreadyExistsException("Username or email already in use");
		}
		Users user = new Users();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(request.getPassword());
		user.setFirstName(request.getFirstName());
		user.setLastName(request.getLastName());
		user = usersRepository.save(user);
		UserResponse response = new UserResponse();
		response.setEmail(user.getEmail());
		response.setId(user.getId());
		response.setUsername(user.getUsername());
		response.setLastName(user.getLastName());
		response.setFirstName(user.getFirstName());
		response.setCreatedAt(com.qurilo.login.utils.UtilityMethods.dateFormater(user.getCreatedAt()));
		response.setUpdatedAt(com.qurilo.login.utils.UtilityMethods.dateFormater(user.getUpdatedAt()));
		return CustomApiResponse.created("User registered successfully", response);
	}

	@Cacheable(value = "users", key = "#usernameOrEmail")
	public CustomApiResponse<UserResponse> getUserDetails(String usernameOrEmail) {
		Users user = usersRepository.findByUsername(usernameOrEmail)
				.or(() -> usersRepository.findByEmail(usernameOrEmail))
				.orElseThrow(() -> new UserNotFoundException("User not found"));
		UserResponse response = new UserResponse();
		response.setEmail(user.getEmail());
		response.setId(user.getId());
		response.setUsername(user.getUsername());
		response.setLastName(user.getLastName());
		response.setFirstName(user.getFirstName());
		response.setCreatedAt(com.qurilo.login.utils.UtilityMethods.dateFormater(user.getCreatedAt()));
		response.setUpdatedAt(com.qurilo.login.utils.UtilityMethods.dateFormater(user.getUpdatedAt()));
		return CustomApiResponse.ok("User retrieved", response);
	}

	@Cacheable(value = "users", key = "#userId")
	public CustomApiResponse<UserResponse> getUserById(Long userId) {
		if (userId == null || userId <= 0) {
			throw new InvalidUserIdException("User ID cannot be null or less than 1");
		}

		Users user = usersRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

		UserResponse response = new UserResponse();
		response.setEmail(user.getEmail());
		response.setId(user.getId());
		response.setUsername(user.getUsername());
		response.setLastName(user.getLastName());
		response.setFirstName(user.getFirstName());
		response.setCreatedAt(com.qurilo.login.utils.UtilityMethods.dateFormater(user.getCreatedAt()));
		response.setUpdatedAt(com.qurilo.login.utils.UtilityMethods.dateFormater(user.getUpdatedAt()));
		return CustomApiResponse.ok("User found successfully", response);
	}

}
