package com.hotel.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResonseDto {
    private String userName;
    private String password;
    private String role;
    private boolean active;
}
