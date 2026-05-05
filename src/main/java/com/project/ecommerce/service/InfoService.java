package com.project.ecommerce.service;

import com.project.ecommerce.dto.InfoDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InfoService {
    public InfoDTO getInfo() {
        return new InfoDTO("E-Commerce Backend", "1.0",
                "Priyakant Kumar", LocalDateTime.now());
    }
}
