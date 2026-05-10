package com.project.ecommerce.dto;

import lombok.Getter;

@Getter
public class OrderDTO {
    private Long id;
    private String productName;
    private Double price;

    public OrderDTO(Long id, String productName, Double price) {
        this.id = id;
        this.productName = productName;
        this.price = price;
    }
}
