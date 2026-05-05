package com.project.ecommerce.service;

import com.project.ecommerce.dto.StatusResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StatusService {

    public String getHello() {
        return "Hello, Backend is running 🚀";
    }

    public StatusResponseDTO getStatus() {
        return new StatusResponseDTO("UP", "E-commerce Backend",
                LocalDateTime.now().toString());
    }


}
