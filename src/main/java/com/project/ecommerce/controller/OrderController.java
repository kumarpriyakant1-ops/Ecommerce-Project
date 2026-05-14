package com.project.ecommerce.controller;

import com.project.ecommerce.dto.ApiResponseDTO;
import com.project.ecommerce.dto.OrderDTO;
import com.project.ecommerce.service.OrderService;
import jakarta.validation.Valid;
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

    @GetMapping("/{id}")
    public ApiResponseDTO<OrderDTO> getOrderById(@PathVariable Long id){
        OrderDTO order = orderService.getOrderById(id);
        return new ApiResponseDTO<>(
                "Order fetched successfully",
                order
        );
    }

    @PutMapping("{id}")
    public ApiResponseDTO<OrderDTO> updateOrder(@PathVariable Long id,
                                                @Valid @RequestBody OrderDTO orderDTO){
        OrderDTO order = orderService.updateOrder(id, orderDTO);
        return new ApiResponseDTO<>(
                "Order updated successfully",
                order
        );
    }


    @GetMapping("/search/byPriceGreaterThan")
    public ApiResponseDTO<List<OrderDTO>> getOrdersByPriceGreaterThan(@RequestParam Double price){
        List<OrderDTO> orders = orderService.getOrdersByPriceGreaterThan(price);
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

    @GetMapping("/search/by-product")
    public ApiResponseDTO<List<OrderDTO>> getOrdersByProductName(@RequestParam String product){
        List<OrderDTO> orders = orderService.getOrdersByProductName(product);
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

    @GetMapping("/")
    public ApiResponseDTO<Page<OrderDTO>> getPaginatedOrders(@RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "5") int size){
       Page<OrderDTO> order =orderService.getPaginatedOrders(page, size);

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
    @DeleteMapping("/{id}")
    public ApiResponseDTO<String> deleteOrder(@PathVariable Long id){
        orderService.deleteOrder(id);
        return new ApiResponseDTO<>(
                "Order deleted successfully",
                null
        );
    }
}
