package com.project.ecommerce.repository;

import com.project.ecommerce.dto.OrderDTO;
import com.project.ecommerce.entity.Order;
import org.apache.logging.log4j.simple.internal.SimpleProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long > {

    @Query("SELECT o FROM Order o WHERE o.price > :price")
    List<Order> findOrdersGreaterThan(Double price);

    @Query(value = "Select * from orders where product_name = :product", nativeQuery = true)
    List<Order> findByProductName(String product);
}
