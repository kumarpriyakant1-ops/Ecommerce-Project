package com.project.ecommerce.dto;

import jakarta.persistence.Column;

public class UserDTO {
    private Long id;
    private String userName;
    private String email;

    public UserDTO(Long id, String userName, String email) {
        this.id = id;
        this.userName = userName;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }
}
