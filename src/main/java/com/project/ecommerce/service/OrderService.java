package com.project.ecommerce.service;
import com.project.ecommerce.dto.OrderDTO;
import com.project.ecommerce.entity.Order;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.repository.OrderRepository;
import com.project.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;


    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Transactional
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

    public OrderDTO getOrderById(Long id) {
        if (id < 0) {
            logger.warn("Id can not be negative {}", id);
            throw new IllegalArgumentException("Id cannot be negative");
        }
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Order is not found with id: {}", id);
                    return new RuntimeException("Order is not found with id: " + id);
                 });
        logger.info("Order fetched successfully with id: {}", id);
        return mapToDTO(order);

    }

    private OrderDTO mapToDTO(Order order){
        return new OrderDTO(
                order.getId(),
                order.getProductName(),
                order.getPrice(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public List<OrderDTO> getOrdersByPriceGreaterThan(Double price) {
        logger.info("Fetching product greater than price : {}", price);
        return orderRepository.findOrdersGreaterThan(price)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByProductName(String product) {
        logger.info("Fetching product: {}", product);
        return orderRepository.findByProductName(product)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Page<OrderDTO> getPaginatedOrders(int page, int size) {
        if(page < 0){
            logger.warn("Invalid Page Number {}",page);
            throw new IllegalArgumentException("Invalid Page Number {}" +page);
        }
        if(size < 0){
            logger.warn("Invalid Size Number {}",size);
            throw new IllegalArgumentException("Invalid Size Number {}" +size);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> order = orderRepository.findByActiveTrue(pageable);
        return order.map(this::mapToDTO);    }


    public List<OrderDTO> getSortedOrders(String sortBy) {
        logger.info("Sorting orders by field: {}",sortBy);
        List<Order> order = orderRepository.findAll(Sort.by(sortBy));
        return order.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void deleteOrder(Long id){
        Order order = orderRepository.findById(id)
                        .orElseThrow(() -> {
                            logger.error("Order not fount with id: {}", id);
                            return new RuntimeException("Order not found with id: " + id);
                        });
        order.setActive(false);
        orderRepository.save(order);
    }

    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        if (id < 0) {
            logger.warn("Id can not be negative {}", id);
            throw new IllegalArgumentException("Id cannot be negative");
        }
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Order not fount with id: {}", id);
                    return new RuntimeException("Order not found with id: " + id);
                });
        if(!order.getActive()){
            logger.warn("Order is inactive");
            throw new RuntimeException("Order is inactive");
        }
        order.setProductName(orderDTO.getProductName());
        order.setPrice(orderDTO.getPrice());
        Order updatedOrder = orderRepository.save(order);
        logger.info("Order updated successfully with id: {}", id);
        return mapToDTO(updatedOrder);
    }

}
