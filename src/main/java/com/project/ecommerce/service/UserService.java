package com.project.ecommerce.service;

import com.project.ecommerce.dto.UserDTO;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> saveAllUsers(List<User> users) {
            return userRepository.saveAll(users);
    }

    private UserDTO mapToDTO(User  user){
        return new UserDTO(
                user.getId(),
                user.getUserName(),
                user.getEmail()
        );
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user =  userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        return mapToDTO(user);
    }

    public UserDTO updateUser(Long id, User userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setUserName(userRequest.getUserName());
        user.setEmail(userRequest.getEmail());
        User updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(("User Not Found With id: " +id)));
        userRepository.delete(user);
    }

    public UserDTO updatedUserPartially(Long id, User userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        if(userRequest.getUserName()  != null){
            user.setUserName(userRequest.getUserName());
        }
        if(userRequest.getEmail() != null){
            user.setEmail(userRequest.getEmail());
        }
        return mapToDTO(user);
    }
}
