package com.hotel.user_service.controller;

import com.hotel.user_service.dto.UpdateUserRequest;
import com.hotel.user_service.dto.UserListResponseDto;
import com.hotel.user_service.dto.UserRequestDto;
import com.hotel.user_service.dto.UserResonseDto;
import com.hotel.user_service.dto.UserResponseById;
import com.hotel.user_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/user")
@CrossOrigin("http://localhost:5173/")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResonseDto> userRegistration(@RequestBody @Validated UserRequestDto userRequestDto){
        return ResponseEntity.ok(userService.userRegistration(userRequestDto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseById> getUserDetails(@PathVariable("id") Long id){
        return ResponseEntity.ok(userService.getUserDetails(id));
    }
    @PatchMapping("/update")
    public ResponseEntity<Map<String,String>> updateUserById(@RequestBody UpdateUserRequest userRequest){
        return ResponseEntity.ok(userService.updateUserById(userRequest));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String,String >> deleteUserById(@PathVariable("id") Long id){
        return ResponseEntity.ok(userService.deleteUserById(id));
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<UserListResponseDto>> getAllUserDetails(){
        return ResponseEntity.ok(userService.getAllDetails());
    }
}
