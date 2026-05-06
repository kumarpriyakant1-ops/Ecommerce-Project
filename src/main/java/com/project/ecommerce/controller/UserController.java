package com.project.ecommerce.controller;

import com.project.ecommerce.dto.UserDTO;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser( @Valid @RequestBody User user){
        return userService.saveUser(user);
    }

    @PostMapping("/batch")
    public List<User> createUser(@RequestBody List<User> users){
        return userService.saveAllUsers(users);
    }
    @GetMapping
    public List<UserDTO> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @Valid @RequestBody User userRequest){
        return userService.updateUser(id, userRequest);
    }

    @PatchMapping("/{id}")
    public  UserDTO updatedUserPartially(@PathVariable Long id, @RequestBody User userRequest){
        return userService.updatedUserPartially(id, userRequest);
    }


    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return "User deleted successfully";
    }

}
