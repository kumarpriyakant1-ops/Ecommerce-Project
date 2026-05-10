package com.project.ecommerce.controller;

import com.project.ecommerce.dto.OrderDTO;
import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/user/{userId}")
    public OrderDTO createOrder(@PathVariable Long userId, @RequestBody OrderDTO orderDTO){
        return orderService.saveOrder(userId, orderDTO);
    }
}
