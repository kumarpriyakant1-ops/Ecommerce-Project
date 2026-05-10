package com.project.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private Double price;

    @JsonBackReference
    @JoinColumn(name = "user_id")
    @ManyToOne
    private User user;

    public Order(Long id, String productName, Double price, User user) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.user = user;
    }

    public Order() {

    }


}
