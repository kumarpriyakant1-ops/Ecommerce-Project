package com.project.ecommerce.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponseDTO {
    private String errorCode;
    private  String message;
    private  int status;
    private LocalDateTime timestamp;

    public ErrorResponseDTO(String errorCode,String message, int status, LocalDateTime timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.errorCode = errorCode;
    }
}
