package com.hotel.user_service.dto;

public class UserRequestDto {
    private String userName;
    private String password;
    private Integer role;
    private boolean active;

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Integer getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
