package com.project.ecommerce.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
public class ErrorResponseDTO {
    private String errorCode;
    private List<String> message;
    private  int status;
    private LocalDateTime timestamp;

    public ErrorResponseDTO(String errorCode, List<String> message, int status, LocalDateTime timestamp) {
        this.errorCode = errorCode;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }
}
