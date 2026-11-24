
package com.hirehub.dto;

import java.nio.charset.StandardCharsets;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;

public class RegisterUserRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    @Size(max = 254, message = "Email cannot exceed 254 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must contain at least 8 characters")
    private String password;

    @AssertTrue(message = "Password cannot exceed 72 UTF-8 bytes")
    public boolean isPasswordWithinBcryptLimit() {
        return password == null ||
                password.getBytes(StandardCharsets.UTF_8).length <= 72;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
