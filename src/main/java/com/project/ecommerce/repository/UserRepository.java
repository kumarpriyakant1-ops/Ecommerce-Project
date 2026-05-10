package com.project.ecommerce.repository;

import com.project.ecommerce.dto.UserDTO;
import com.project.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUserNameContaining(String name);
    List<User> findByEmailAndUserName(String email, String name);
}
