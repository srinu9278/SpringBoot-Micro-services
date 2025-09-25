package com.hotel.user_service.service;

import com.hotel.user_service.dto.UpdateUserRequest;
import com.hotel.user_service.dto.UserListResponseDto;
import com.hotel.user_service.dto.UserRequestDto;
import com.hotel.user_service.dto.UserResonseDto;
import com.hotel.user_service.dto.UserResponseById;
import com.hotel.user_service.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserResonseDto userRegistration(UserRequestDto userRequestDto);

    UserResponseById getUserDetails(Long id);

    Map<String,String> updateUserById(UpdateUserRequest userRequest);

    Map<String, String> deleteUserById(Long id);

    List<UserListResponseDto> getAllDetails();
}
