package com.project.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private String productName;
    private Double price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
