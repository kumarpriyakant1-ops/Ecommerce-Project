package com.project.ecommerce.service;

import com.project.ecommerce.dto.OrderDTO;
import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.repository.OrderRepository;
import com.project.ecommerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;


    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public OrderDTO saveOrder(Long userId, OrderDTO orderDTO) {
        logger.info("Creating order with User Id {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with id: {}", userId);
                    return new RuntimeException("User not found with id: " + userId);
                });
        Order order = new Order();
        order.setProductName(orderDTO.getProductName());
        order.setPrice(orderDTO.getPrice());
        order.setUser(user);
        Order saveOrder = orderRepository.save(order);
        logger.info("Order created successfully with Id: {}", userId);
        return mapToDTO(saveOrder);
    }

    private OrderDTO mapToDTO(Order order){
        return new OrderDTO(
                order.getId(),
                order.getProductName(),
                order.getPrice()
        );
    }
}
