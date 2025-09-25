package com.hotel.user_service.enums;

import com.hotel.user_service.exception.InvalidRoleException;

public enum UserRoles {
    USER(0,"User"),
    STAFF(1,"Staff"),
    GUEST(2,"Guest");

    private final Integer value;
    private final String name;

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    UserRoles(Integer value, String name) {
        this.value = value;
        this.name=name;
    }
    public static String getUserRole(Integer value){
        for(UserRoles userRoles : UserRoles.values()){
            if(userRoles.getValue() == value){
                return userRoles.getName();
            }
        }
        throw new InvalidRoleException("Invalid role value: " + value);
    }
}
