package com.project.ecommerce.controller;

import com.project.ecommerce.dto.*;
import com.project.ecommerce.service.RefreshTokenService;
import com.project.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public UserController(UserService userService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signup")
    public ApiResponseDTO<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO){
        UserDTO saveUser = userService.saveUser(userDTO);
        return new ApiResponseDTO<>(
                "User Created Successfully",
                saveUser
        );
    }

    @PostMapping("/login")
    public ApiResponseDTO<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO){
        LoginResponseDTO response = userService.login(loginRequestDTO);
        return new ApiResponseDTO<>(
                "Login Successfully",
                response
        );
    }

    @PostMapping("/logout")
    public ApiResponseDTO<RefreshTokenRequestDTO> logout(@Valid @RequestBody RefreshTokenRequestDTO token ){
        refreshTokenService.logout(token.getRefreshToken());
        return new ApiResponseDTO<>(
                "Logout Successfully",
                null
        );
    }

    @PostMapping("/refresh-token")
    public ApiResponseDTO<String> refreshToken(@RequestParam String refreshTokenValue){
        String accessToken = refreshTokenService.generateAccessToken(refreshTokenValue);
        return new ApiResponseDTO<>(
                "New access token generated",
                accessToken
        );
    }

    @PostMapping("/rotate-token")
    public ApiResponseDTO<LoginResponseDTO> rotateToken(@RequestParam String refreshTokenValue){
        LoginResponseDTO accessToken = refreshTokenService.rotateToken(refreshTokenValue);
        return new ApiResponseDTO<>(
                "Token rotated successfully",
                accessToken
        );
    }
    @GetMapping("/{id}")
    public ApiResponseDTO<UserDTO> getUserById(@PathVariable Long id){
        UserDTO user = userService.getUserById(id);
        return new ApiResponseDTO<>(
                "User fetched successfully",
                user
        );
    }

    @PutMapping("/update/{id}")
    public ApiResponseDTO<UserDTO> updateUser(@PathVariable Long id,
                                              @Valid @RequestBody UserDTO userDTO){
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return new ApiResponseDTO<>(
                "User Updated Successfully",
                updatedUser
        );
    }

    @PatchMapping("/{id}")
    public  ApiResponseDTO<UserDTO> updatedUserPartially(@PathVariable Long id,
                                                         @RequestBody UserDTO userDto){
        UserDTO user = userService.updatedUserPartially(id, userDto);
        return  new ApiResponseDTO<>(
                "User updated successfully",
                user
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponseDTO<String> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return new ApiResponseDTO<>(
                "User deleted successfully",
                null
        );
    }

    @GetMapping("/paginated")
    public ApiResponseDTO<Page<UserDTO>> getPaginatedUsers(@RequestParam int page, @RequestParam int size){
        Page<UserDTO> user =  userService.getPaginatedUsers(page, size);
        return new ApiResponseDTO<>(
                "User fetched successfully",
                user
        );
    }

    @GetMapping("/sorted")
    public ApiResponseDTO<Page<UserDTO>> getUsersSorted(@RequestParam int page,
                                        @RequestParam int size, @RequestParam String sortBy){
        Page<UserDTO> user = userService.getUserSorted(page, size, sortBy);
        return new ApiResponseDTO<>(
                "User fetched successfully",
                user
        );

    }

    @GetMapping("/search")
    public ApiResponseDTO<List<UserDTO>> searchUserByName(@RequestParam String name){
        List<UserDTO> users = userService.searchUserByName(name);
        if(users.isEmpty()){
            return new ApiResponseDTO<>(
                    "No Users found by name: " +name,
                    users
            );
        }
        return new ApiResponseDTO<>(
                "User fetched successfully",
                users
        );
    }

    @GetMapping("/findByEmailAndUserName")
    public ApiResponseDTO<List<UserDTO>> findByEmailAndUserName(@RequestParam String name, @RequestParam String email){
        List<UserDTO> users =  userService.findByEmailAndUserName(name, email);
        if(users.isEmpty()){
            return new ApiResponseDTO<>(
                    "No Users found",
                    users
            );
        }
        return new ApiResponseDTO<>(
                "User fetched successfully",
                users
        );
    }

}
