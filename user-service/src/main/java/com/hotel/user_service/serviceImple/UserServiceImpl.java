package com.hotel.user_service.serviceImple;

import com.hotel.user_service.client.NotificationClient;
import com.hotel.user_service.dto.UpdateUserRequest;
import com.hotel.user_service.dto.UserListResponseDto;
import com.hotel.user_service.dto.UserResponseById;
import com.hotel.user_service.enums.ErrorCode;
import com.hotel.user_service.enums.UserRoles;
import com.hotel.user_service.dto.UserRequestDto;
import com.hotel.user_service.dto.UserResonseDto;
import com.hotel.user_service.exception.CustomException;
import com.hotel.user_service.model.User;
import com.hotel.user_service.repository.UserRepository;
import com.hotel.user_service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationClient notificationClient;

    @Override
    public UserResonseDto userRegistration(UserRequestDto userRequestDto) {
        logger.info("Starting user registration for username: {}", userRequestDto.getUserName());
        
        // Validate input
        validateUserRequest(userRequestDto);
        
        // Check if user already exists
        if (userRepository.findByUserName(userRequestDto.getUserName()).isPresent()) {
            logger.warn("User registration failed - username already exists: {}", userRequestDto.getUserName());
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS, 
                "Username '" + userRequestDto.getUserName() + "' is already taken");
        }
        
        try {
            User user = new User();
            user.setUserName(userRequestDto.getUserName());
            user.setPassword(userRequestDto.getPassword());
            user.setRole(UserRoles.getUserRole(userRequestDto.getRole()));
            user.setActive(userRequestDto.isActive());
            
            User savedUser = userRepository.save(user);
            logger.info("User registered successfully with ID: {}", savedUser.getId());
            
            // Send notification to Slack
            try {
                Map<String, Object> userData = new HashMap<>();
                userData.put("userId", savedUser.getId());
                userData.put("userName", savedUser.getUserName());
                userData.put("role", savedUser.getRole());
                userData.put("active", savedUser.isActive());
                userData.put("createdAt", savedUser.getCreatedAt());
                
                notificationClient.handleUserEvent("USER_REGISTERED", userData);
                logger.info("User registration notification sent successfully");
            } catch (Exception e) {
                logger.error("Failed to send user registration notification: {}", e.getMessage());
                // Don't fail the user registration if notification fails
            }
            
            UserResonseDto userResponse = UserResonseDto.builder()
                    .userName(userRequestDto.getUserName())
                    .password(userRequestDto.getPassword())
                    .role(UserRoles.getUserRole(userRequestDto.getRole()))
                    .active(userRequestDto.isActive())
                    .build();
            
            return userResponse;
            
        } catch (Exception e) {
            logger.error("Error during user registration: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.DATABASE_ERROR, 
                "Failed to save user: " + e.getMessage(), e);
        }
    }

    @Override
    public UserResponseById getUserDetails(Long id) {
        if(id == null){
            throw new CustomException(ErrorCode.USER_NOT_FOUND,"for id :"+id);
        }
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new CustomException(ErrorCode.DATA_NOT_FOUND,"for id " + id);
        }
        UserResponseById userResponse = UserResponseById.builder()
                .name(user.get().getUserName())
                .role(user.get().getRole()).state(user.get().isActive()).build();
        return userResponse;
    }

    @Override
    public Map<String,String> updateUserById(UpdateUserRequest userRequest) {
        Map<String,String> map = new HashMap<>();
        if(userRequest.getId() == null){
            throw new CustomException(ErrorCode.USER_NOT_FOUND,"for id :" + userRequest.getId());
        }
        Optional<User> user = userRepository.findById(userRequest.getId());
        if(user.isEmpty()){
            throw new CustomException(ErrorCode.DATA_NOT_FOUND,"for this id :" + userRequest.getId());
        }
        try {
            User updateUser = user.get();
            updateUser.setUserName(userRequest.getName()!=null ? userRequest.getName() : user.get().getUserName());
            updateUser.setPassword(userRequest.getPassword()!=null? userRequest.getPassword() : user.get().getPassword());
            updateUser.setRole(userRequest.getRole()!=null ? UserRoles.getUserRole(userRequest.getRole()) : user.get().getRole());
            userRepository.save(updateUser);
        }catch (CustomException e){
            throw new CustomException(ErrorCode.DATABASE_ERROR,"for user"+user);
        }
        return Map.of("Message","updated the user data successfully");
    }

    @Override
    public Map<String, String> deleteUserById(Long id) {
        if(id == null){
            throw new CustomException(ErrorCode.USER_NOT_FOUND,"For this id :" + id);
        }
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new CustomException(ErrorCode.DATA_NOT_FOUND,"for this user:"+user.get());
        }
        userRepository.deleteById(id);
        return Map.of("Message","Deleted the user successfully");
    }

    @Override
    public List<UserListResponseDto> getAllDetails() {
        logger.info("Retrieving all user details from database");
        try {
            List<User> users = userRepository.findAll();
            logger.info("Successfully retrieved {} users from database", users.size());
            
            List<UserListResponseDto> userListResponse = users.stream()
                .map(user -> UserListResponseDto.builder()
                    .id(user.getId())
                    .userName(user.getUserName())
                    .role(user.getRole())
                    .active(user.isActive())
                    .build())
                .collect(Collectors.toList());

            return userListResponse;
        } catch (Exception e) {
            logger.error("Error retrieving all users: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.DATABASE_ERROR, 
                "Failed to retrieve users: " + e.getMessage(), e);
        }
    }

    private void validateUserRequest(UserRequestDto userRequestDto) {
        if (userRequestDto.getUserName() == null || userRequestDto.getUserName().trim().isEmpty()) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING, "Username is required");
        }
        
        if (userRequestDto.getPassword() == null || userRequestDto.getPassword().trim().isEmpty()) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING, "Password is required");
        }
        
        if (userRequestDto.getRole() == null) {
            throw new CustomException(ErrorCode.REQUIRED_FIELD_MISSING, "Role is required");
        }
        
        // Validate role value
        try {
            UserRoles.getUserRole(userRequestDto.getRole());
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_ROLE, 
                "Invalid role value: " + userRequestDto.getRole() + ". Valid values are: 0, 1, 2");
        }
    }
}
