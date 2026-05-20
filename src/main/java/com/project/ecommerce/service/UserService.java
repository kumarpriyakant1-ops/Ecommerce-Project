package com.project.ecommerce.service;
import com.project.ecommerce.dto.ForgotPasswordRequestDTO;
import com.project.ecommerce.dto.LoginRequestDTO;
import com.project.ecommerce.dto.LoginResponseDTO;
import com.project.ecommerce.dto.UserDTO;
import com.project.ecommerce.entity.PasswordResetToken;
import com.project.ecommerce.entity.RefreshToken;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.Role;
import com.project.ecommerce.repository.PasswordResetTokenRepository;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    public UserService(JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    public UserDTO saveUser(UserDTO userDTO) {
        logger.info("Creating user with email: {}", userDTO.getEmail());
        User user = new User();
        user.setUserName(userDTO.getUserName());
        user.setEmail(userDTO.getEmail());
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User saveUser = userRepository.save(user);
        logger.info("User created successfully {}", userDTO.getEmail());
        try {

            String emailBody = String.format("""
                Dear %s,

                Welcome to Ecommerce Platform!

                Your account has been successfully registered with us.

                You can now securely log in to the platform and access our services.

                For security reasons, please do not share your login credentials with anyone.

                If you have any questions or need assistance, feel free to contact our support team.

                Thank you for choosing Ecommerce Platform.

                Best regards,
                Ecommerce Platform Team
                """,

                    saveUser.getUserName()
            );
            emailService.sendMail(
                    saveUser.getEmail(),
                    "Welcome to Ecommerce Platform",
                    emailBody
            );
            logger.info("Welcome email sent successfully to {}", saveUser.getEmail()
            );
        } catch (Exception ex){
            logger.error("Failed to send welcome email: {}", ex.getMessage()
            );
        }
        return mapToDTO(saveUser);
    }
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        logger.info("Fetching User with Email: {}", loginRequestDTO.getEmail());
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow( () -> {
                    logger.error("User not found with Email: {}", loginRequestDTO.getEmail());
                    return new RuntimeException("User not found with Email: " + loginRequestDTO.getEmail());
                });

        boolean matches = passwordEncoder
                .matches(loginRequestDTO.getPassword(), user.getPassword());
        if(!matches){
            logger.error("Password Invalid !");
            throw new RuntimeException("Password Invalid !");
        }
//        String accessToken = jwtService.generateToken(user.getEmail());
        String accessToken = jwtService.generateToken(user);
        logger.debug("Access token generated successfully");
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        logger.debug("Refresh token generated successfully");
        return new LoginResponseDTO(accessToken, refreshToken.getToken());
    }

    public void forgotPassword(ForgotPasswordRequestDTO requestDTO) {
        logger.info("Forgot password request for email: {}", requestDTO.getEmail());
        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", requestDTO.getEmail());
                    return new RuntimeException("User not found with email: " +requestDTO.getEmail());
                });

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        passwordResetToken.setUser(user);
        passwordResetTokenRepository.save(passwordResetToken);

        String resetLink = "http://localhost:8080/api/users/reset-password?token=" + token;

        String emailBody = String.format("""
            
            Dear %s,
            
            We received a request to reset your password.
            
            Please click the link below to reset your password:
            
            %s
            
            This link will expire in 15 minutes.
            
            If you did not request this, please ignore this email.
            
            Regards,
            Ecommerce Platform Team
            """,

                user.getUserName(),
                resetLink
        );

        try{
            emailService.sendMail(
                    user.getEmail(),
                    "Reset your password",
                    emailBody
            );
        }catch(Exception ex){
            logger.error("Failed to send reset email {}", ex.getMessage());

        }
    }

    public void resetPassword(String token, String newPassword) {
        logger.info("Reset password request received");
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    logger.error("Invalid reset token");
                    return new RuntimeException("Invalid reset token");
                });

        if(resetToken.getExpiryDate().isBefore(LocalDateTime.now())){
            logger.error("Reset token got expired");
            throw new RuntimeException("Reset token got expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);
        logger.info("Password reset successful for {}", user.getEmail());
    }

    private UserDTO mapToDTO(User  user){
        return new UserDTO(
                user.getId(),
                user.getUserName(),
                null,
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

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        if (id < 0) {
            logger.warn("Id can not be negative {}", id);
            throw new IllegalArgumentException("Id cannot be negative");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() ->{
                    logger.error("User not found for update with id: {}", id);
                    return new RuntimeException("User not found with id: " + id);
                });
        user.setUserName(userDTO.getUserName());
        user.setEmail(userDTO.getEmail());
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

    public UserDTO updatedUserPartially(Long id, UserDTO userDTO) {
        if (id < 0) {
            logger.warn("Id can not be negative {}", id);
            throw new IllegalArgumentException("Id cannot be negative");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with id: {}", id);
                    return new RuntimeException("User not found with id: " + id);
                });
        if(userDTO.getUserName()  != null){
            user.setUserName(userDTO.getUserName());
        }
        if(userDTO.getEmail() != null){
            user.setEmail(userDTO.getEmail());
        }
        User updatedUser = userRepository.save(user);

        logger.info("User patched successfully with id: {}",id);
        return mapToDTO(updatedUser);
    }

    public Page<UserDTO> getPaginatedUsers(int page, int size) {

        if (page < 0 || size <= 0 || size > 100) {
            logger.warn("Invalid Page or size");
            throw new IllegalArgumentException("Invalid page or size");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(this::mapToDTO);
    }

    public Page<UserDTO> getUserSorted(int page, int size, String sortBy) {
        if (page < 0 || size <= 0 || size > 100) {
            logger.warn("Invalid Page or size");
            throw new IllegalArgumentException("Invalid page or size");
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
