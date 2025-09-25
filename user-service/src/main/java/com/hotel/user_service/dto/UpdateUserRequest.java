package com.hotel.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @NonNull
    private Long id;
    private String name;
    private String password;
    private Integer role;
}
