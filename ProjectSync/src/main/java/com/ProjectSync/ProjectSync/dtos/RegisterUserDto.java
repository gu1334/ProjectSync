package com.ProjectSync.ProjectSync.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class RegisterUserDto {
    private String email;

    private String password;

    public String getEmail() {
        return email;
    }


    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
