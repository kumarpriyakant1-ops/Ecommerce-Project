package com.project.ecommerce.service;
import com.project.ecommerce.dto.UserDTO;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.repository.UserRepository;
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
public class UserService {

    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        logger.info("Creating user with email: {}", user.getEmail());
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
        if (id < 0) {
            logger.warn("Id can not be negative {}", id);
            throw new IllegalArgumentException("Id cannot be negative");
        }
        User user =  userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with id: {}", id);
                     return new RuntimeException("User not found with id: " + id);
                });
        logger.info("User fetched successfully with id: {}", id);
        return mapToDTO(user);
    }

    public UserDTO updateUser(Long id, User userRequest) {
        if (id < 0) {
            logger.warn("Id can not be negative {}", id);
            throw new IllegalArgumentException("Id cannot be negative");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() ->{
                    logger.error("User not found for update with id: {}", id);
                    return new RuntimeException("User not found with id: " + id);
                });
        user.setUserName(userRequest.getUserName());
        user.setEmail(userRequest.getEmail());
        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully with id: {}", id);
        return mapToDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        if (id < 0) {
            logger.warn("Id can not be negative {}", id);
            throw new IllegalArgumentException("Id cannot be negative");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with id: {}", id);
                    return new RuntimeException("User not found with id: " + id);
                });
        userRepository.delete(user);
        logger.info("User deleted successfully with id: {}", id);

    }

    public UserDTO updatedUserPartially(Long id, User userRequest) {
        if (id < 0) {
            logger.warn("Id can not be negative {}", id);
            throw new IllegalArgumentException("Id cannot be negative");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with id: {}", id);
                    return new RuntimeException("User not found with id: " + id);
                });
        if(userRequest.getUserName()  != null){
            user.setUserName(userRequest.getUserName());
        }
        if(userRequest.getEmail() != null){
            user.setEmail(userRequest.getEmail());
        }
        return mapToDTO(user);
    }

    public Page<UserDTO> getPaginatedUsers(int page, int size) {
        if(page < 0){
            logger.warn("Invalid Page Number {}",page);
            throw new IllegalArgumentException("Invalid Page Number {}" +page);
        }
        if(size < 0){
            logger.warn("Invalid Size Number {}",size);
            throw new IllegalArgumentException("Invalid Size Number {}" +size);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(this::mapToDTO);
    }

    public Page<UserDTO> getUserSorted(int page, int size, String sortBy) {
        if(page < 0){
            logger.warn("Invalid Page Number {}",page);
            throw new IllegalArgumentException("Invalid Page Number {}" +page);
        }
        if(size < 0){
            logger.warn("Invalid Size Number {}",size);
            throw new IllegalArgumentException("Invalid Size Number {}" +size);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return userRepository.findAll(pageable).map(this::mapToDTO);
    }


    public List<UserDTO> searchUserByName(String name) {
        logger.info("Searching users with name: {}", name);
        List<User> users = userRepository.findByUserNameContaining(name);
        if (users.isEmpty()){
            logger.error("No users found with name: {}", name);
            throw new RuntimeException("No users found with name: " + name);
        }
        logger.info("Users fetched successfully with name: {}", name);
        return users.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> findByEmailAndUserName(String name, String email) {
        logger.info("Searching users with email and name: {}", name);
        List<User> users = userRepository.findByEmailAndUserName(email,name);
        if (users.isEmpty()){
            logger.error("No users found with Email and Name: {} {}", email, name);
            throw new RuntimeException("No users found with Email and Name: " + email + name );
        }
        return users.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
