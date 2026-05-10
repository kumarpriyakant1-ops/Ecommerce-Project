package com.project.ecommerce.controller;

import com.project.ecommerce.dto.ApiResponseDTO;
import com.project.ecommerce.dto.OrderDTO;
import com.project.ecommerce.dto.UserDTO;
import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/user/{userId}")
    public ApiResponseDTO<OrderDTO>  createOrder(@PathVariable Long userId,
                                                 @RequestBody OrderDTO orderDTO){
        OrderDTO saveOrder = orderService.saveOrder(userId, orderDTO);
        return new ApiResponseDTO<>(
                "Order saved Successfully",
                saveOrder
        );
    }

    @GetMapping("/price")
    public ApiResponseDTO<List<OrderDTO>> findOrdersGreaterThan(@RequestParam Double price){
        List<OrderDTO> orders = orderService.findOrdersGreaterThan(price);
        if(orders.isEmpty()){
            return new ApiResponseDTO<>(
                    "No orders found greater tan price: " + price,
                    orders
            );
        }
        return new ApiResponseDTO<>(
                "Order greater than price: " +price,
                orders
        );
    }

    @GetMapping("/product")
    public ApiResponseDTO<List<OrderDTO>> findByProductName(@RequestParam String product){
        List<OrderDTO> orders = orderService.findByProductName(product);
        if(orders.isEmpty()){
            return new ApiResponseDTO<>(
                    "No orders found for product: " + product,
                    orders
            );
        }
        return new ApiResponseDTO<>(
                "Orders fetched successfully",
                orders
        );
     }

    @GetMapping("/paginated")
    public ApiResponseDTO<Page<OrderDTO>> getPaginatedUsers(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "5") int size){
       Page<OrderDTO> order =orderService.getPaginatedOrder(page, size);

       return new ApiResponseDTO<>(
               "Order Fetched Successfully",
               order
       );
    }

    @GetMapping("/sorted")
    public ApiResponseDTO<List<OrderDTO>> getSortedOrders(@RequestParam String sortBy){
        List<OrderDTO> order =orderService.getSortedOrders(sortBy);
        return new ApiResponseDTO<>(
                "Order sorted Successfully",
                order
        );
    }
}
