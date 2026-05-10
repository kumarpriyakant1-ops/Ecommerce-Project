package com.project.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseDTO<T> {
    private String message;
    private T data;

    public ApiResponseDTO() {
    }

    public ApiResponseDTO(String message, T data) {
        this.message = message;
        this.data = data;
    }
}
