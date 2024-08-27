package com.israr.jwtpractice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    private String username;
    private String password;
    private String email;

    public boolean isAdminRegistration() {
        return email.endsWith(("@admin.com"));
    }
}

