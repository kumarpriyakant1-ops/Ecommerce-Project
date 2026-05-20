package com.project.ecommerce.controller;

import com.project.ecommerce.dto.*;
import com.project.ecommerce.service.EmailService;
import com.project.ecommerce.service.RefreshTokenService;
import com.project.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(
        name = "User APIs",
        description = "User management, authentication, and security APIs"
)
public class UserController {

    @Autowired
    private EmailService emailService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public UserController(UserService userService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @Operation(
            summary = "Register User",
            description = "Create new user account"
    )
    @PostMapping("/signup")
    public ApiResponseDTO<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO){
        UserDTO saveUser = userService.saveUser(userDTO);
        return new ApiResponseDTO<>(
                "User Created Successfully",
                saveUser
        );
    }

    @Operation(
            summary = "Verify Email",
            description = "Verify user email using verification token"
    )
    @PostMapping("/verify-email")
    public ApiResponseDTO<String> verifyEmail(@Valid @RequestBody EmailVerificationRequestDTO requestDTO){
        userService.verifyEmail(requestDTO.getToken());
        return new ApiResponseDTO<>(
                "Email verified successfully",
                null
        );
    }

    @Operation(
            summary = "Login User",
            description = "Authenticate user and generate JWT token"
    )
    @PostMapping("/login")
    public ApiResponseDTO<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO){
        LoginResponseDTO response = userService.login(loginRequestDTO);
        return new ApiResponseDTO<>(
                "Login Successfully",
                response
        );
    }

    @Operation(
            summary = "Logout User",
            description = "Logout the user"
    )
    @PostMapping("/logout")
    public ApiResponseDTO<RefreshTokenRequestDTO> logout(@Valid @RequestBody RefreshTokenRequestDTO token ){
        refreshTokenService.logout(token.getRefreshToken());
        return new ApiResponseDTO<>(
                "Logout Successfully",
                null
        );
    }

    @Operation(
            summary = "Forgot Password",
            description = "Send password reset email with reset token"
    )
    @PostMapping("/forgot-password")
    public ApiResponseDTO<ForgotPasswordRequestDTO> forgotPassword(@RequestBody ForgotPasswordRequestDTO requestDTO){
        userService.forgotPassword(requestDTO);
        return new ApiResponseDTO<>(
                "Reset Email sent",
                null
        );
    }
    @Operation(
            summary = "Reset Password",
            description = "Reset user password using valid reset token"
    )
    @PostMapping("/reset-password")
    public ApiResponseDTO<String> resetPassword(@RequestBody ResetPasswordRequestDTO request){
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return new ApiResponseDTO<>(
                "Password reset successful",
                null
        );
    }

    @Operation(
            summary = "Refresh Token",
            description = "Generate new access token using refresh token"
    )
    @PostMapping("/refresh-token")
    public ApiResponseDTO<String> refreshToken(@RequestParam String refreshTokenValue){
        String accessToken = refreshTokenService.generateAccessToken(refreshTokenValue);
        return new ApiResponseDTO<>(
                "New access token generated",
                accessToken
        );
    }

    @Operation(
            summary = "Rotate Token",
            description = "Generate new access token and refresh token using old refresh token"
    )
    @PostMapping("/rotate-token")
    public ApiResponseDTO<LoginResponseDTO> rotateToken(@RequestParam String refreshTokenValue){
        LoginResponseDTO accessToken = refreshTokenService.rotateToken(refreshTokenValue);
        return new ApiResponseDTO<>(
                "Token rotated successfully",
                accessToken
        );
    }

    @Operation(
            summary = "Get User By ID",
            description = "Fetch user details using user ID"
    )
    @GetMapping("/{id}")
    public ApiResponseDTO<UserDTO> getUserById(@PathVariable Long id){
        UserDTO user = userService.getUserById(id);
        return new ApiResponseDTO<>(
                "User fetched successfully",
                user
        );
    }

    @Operation(
            summary = "Update User",
            description = "Update existing user details"
    )
    @PutMapping("/update/{id}")
    public ApiResponseDTO<UserDTO> updateUser(@PathVariable Long id,
                                              @Valid @RequestBody UserDTO userDTO){
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return new ApiResponseDTO<>(
                "User Updated Successfully",
                updatedUser
        );
    }

    @Operation(
            summary = "Update User Partially",
            description = "Update existing user partially"
    )
    @PatchMapping("/{id}")
    public  ApiResponseDTO<UserDTO> updatedUserPartially(@PathVariable Long id,
                                                         @RequestBody UserDTO userDto){
        UserDTO user = userService.updatedUserPartially(id, userDto);
        return  new ApiResponseDTO<>(
                "User updated successfully",
                user
        );
    }

    @Operation(
            summary = "Delete User",
            description = "Delete user account and related tokens"
    )
    @DeleteMapping("/{id}")
    public ApiResponseDTO<String> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return new ApiResponseDTO<>(
                "User deleted successfully",
                null
        );
    }

    @Operation(
            summary = "Get paginated users",
            description = "Fetch users in a paginated format by providing page number and page size"
    )
    @GetMapping("/paginated")
    public ApiResponseDTO<Page<UserDTO>> getPaginatedUsers(@RequestParam int page, @RequestParam int size){
        Page<UserDTO> user =  userService.getPaginatedUsers(page, size);
        return new ApiResponseDTO<>(
                "User fetched successfully",
                user
        );
    }

    @Operation(
            summary = "Get sorted users with pagination",
            description = "Fetch users with pagination and sorting based on a given field (e.g., userName, email, createdAt)"
    )
    @GetMapping("/sorted")
    public ApiResponseDTO<Page<UserDTO>> getUsersSorted(@RequestParam int page,
                                        @RequestParam int size, @RequestParam String sortBy){
        Page<UserDTO> user = userService.getUserSorted(page, size, sortBy);
        return new ApiResponseDTO<>(
                "User fetched successfully",
                user
        );

    }

    @Operation(
            summary = "Search Users",
            description = "Search users by username"
    )
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

    @Operation(
            summary = "Search Users",
            description = "Search users by username or email"
    )
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
