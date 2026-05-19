package com.project.ecommerce.service;
import com.project.ecommerce.dto.LoginRequestDTO;
import com.project.ecommerce.dto.LoginResponseDTO;
import com.project.ecommerce.dto.UserDTO;
import com.project.ecommerce.entity.RefreshToken;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.enums.Role;
import com.project.ecommerce.repository.UserRepository;
import com.project.ecommerce.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Test
    void saveUser_ShouldSaveUserSuccessfully() {
        //Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName("Test User");
        userDTO.setEmail("Test@gmail.com");
        userDTO.setPassword("Test@123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUserName("Test User");
        savedUser.setEmail("Test@gmail.com");
        savedUser.setRole(Role.USER);
        savedUser.setPassword("encodedPassword");

        when(passwordEncoder.encode("Test@123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        //Act
        UserDTO response = userService.saveUser(userDTO);

        //Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test User", response.getUserName());
        assertEquals("Test@gmail.com", response.getEmail());

        verify(passwordEncoder, times(1)).encode("Test@123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void login_ShouldLoginSuccessfully(){
        //Arrange
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("test@gmail.com");
        loginRequestDTO.setPassword("Test@123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUserName("Test User");
        savedUser.setEmail("test@gmail.com");
        savedUser.setRole(Role.USER);
        savedUser.setPassword("encodedPassword");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh_token");

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("Test@123", "encodedPassword")).thenReturn(true);
        when(jwtService.generateToken(savedUser)).thenReturn("access_token");
        when(refreshTokenService.createRefreshToken(savedUser)).thenReturn(refreshToken);

        //Act
        LoginResponseDTO response = userService.login(loginRequestDTO);

        //Assert
        assertNotNull(response);
        assertEquals("refresh_token", response.getRefreshToken());
        assertEquals("access_token", response.getAccessToken());

        verify(passwordEncoder, times(1)).matches("Test@123", "encodedPassword");
        verify(userRepository, times(1)).findByEmail("test@gmail.com");
        verify(refreshTokenService, times(1)).createRefreshToken(savedUser);
        verify(jwtService, times(1)).generateToken(savedUser);

    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsInvalid(){
        //Arrange
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("test@gmail.com");
        loginRequestDTO.setPassword("Test@123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUserName("Test User");
        savedUser.setEmail("test@gmail.com");
        savedUser.setRole(Role.USER);
        savedUser.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("Test@123", "encodedPassword"))
                .thenReturn(false);

        //Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.login(loginRequestDTO)
                );
        assertEquals("Password Invalid !", exception.getMessage());

        verify(passwordEncoder, times(1))
                .matches("Test@123", "encodedPassword");
        verify(userRepository, times(1)).findByEmail("test@gmail.com");
        verify(refreshTokenService, never()).createRefreshToken(savedUser);
        verify(jwtService, never()).generateToken(savedUser);
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound(){

        //Arrange
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("test@gmail.com");
        loginRequestDTO.setPassword("Test@123");

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.empty());

        //Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.login(loginRequestDTO)
        );

        assertEquals("User not found with Email: test@gmail.com", exception.getMessage());
        verify(userRepository, times(1)).findByEmail("test@gmail.com");
        verify(refreshTokenService, never()).createRefreshToken(any());
        verify(jwtService, never()).generateToken(any());

    }

    @Test
    void getUserById_ShouldReturnUser(){
        Long id = 1L;

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUserName("Test User");
        savedUser.setEmail("test@gmail.com");
        savedUser.setRole(Role.USER);
        savedUser.setPassword("encodedPassword");

        when(userRepository.findById(id)).thenReturn(Optional.of(savedUser));

        UserDTO response = userService.getUserById(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("Test User", response.getUserName());
        assertEquals("test@gmail.com", response.getEmail());

        verify(userRepository, times(1)).findById(id);
    }

    @Test
    void getUserById_ShouldThrowException_WhenIdIsNegative() {

        // Arrange
        Long userId = -1L;
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUserById(userId)
        );

        assertEquals("Id cannot be negative", exception.getMessage());

        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound(){
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.getUserById(userId)
        );

        assertEquals("User not found with id: 1", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {

        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setUserName("Priyakant");
        user1.setEmail("priya@test.com");
        user1.setRole(Role.USER);

        User user2 = new User();
        user2.setId(2L);
        user2.setUserName("Rahul");
        user2.setEmail("rahul@test.com");
        user2.setRole(Role.ADMIN);

        List<User> users = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserDTO> response = userService.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals("Priyakant", response.get(0).getUserName());
        assertEquals("priya@test.com", response.get(0).getEmail());

        assertEquals("Rahul", response.get(1).getUserName());
        assertEquals("rahul@test.com", response.get(1).getEmail());

        verify(userRepository, times(1)).findAll();
    }
    @Test
    void getAllUsers_ShouldReturnEmptyList() {

        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<UserDTO> response = userService.getAllUsers();

        // Assert
        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(userRepository, times(1)).findAll();
    }
    
    @Test
    void updateUser_ShouldUpdateUserSuccessfully() {

        // Arrange
        Long userId = 1L;

        UserDTO requestDTO = new UserDTO();
        requestDTO.setUserName("Updated User");
        requestDTO.setEmail("updated@test.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUserName("Old User");
        existingUser.setEmail("old@test.com");
        existingUser.setRole(Role.USER);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUserName("Updated User");
        updatedUser.setEmail("updated@test.com");
        updatedUser.setRole(Role.USER);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.save(any(User.class)))
                .thenReturn(updatedUser);

        // Act
        UserDTO response = userService.updateUser(userId, requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(userId, response.getId());
        assertEquals("Updated User", response.getUserName());
        assertEquals("updated@test.com", response.getEmail());

        verify(userRepository, times(1))
                .findById(userId);

        verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {

        // Arrange
        Long userId = 1L;

        UserDTO requestDTO = new UserDTO();
        requestDTO.setUserName("Test");
        requestDTO.setEmail("test@test.com");

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.updateUser(userId, requestDTO)
        );

        assertEquals(
                "User not found with id: 1",
                exception.getMessage()
        );

        verify(userRepository, times(1))
                .findById(userId);

        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void updatedUserPartially_ShouldUpdateOnlyUserName() {

        // Arrange
        Long userId = 1L;

        UserDTO requestDTO = new UserDTO();
        requestDTO.setUserName("Updated User");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUserName("Old User");
        existingUser.setEmail("old@test.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUserName("Updated User");
        updatedUser.setEmail("old@test.com");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.save(any(User.class)))
                .thenReturn(updatedUser);

        // Act
        UserDTO response = userService.updatedUserPartially(userId, requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Updated User", response.getUserName());
        assertEquals("old@test.com", response.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }
    @Test
    void updatedUserPartially_ShouldUpdateOnlyEmail() {

        // Arrange
        Long userId = 1L;

        UserDTO requestDTO = new UserDTO();
        requestDTO.setEmail("updated@test.com");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUserName("Old User");
        existingUser.setEmail("old@test.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUserName("Old User");
        updatedUser.setEmail("updated@test.com");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.save(any(User.class)))
                .thenReturn(updatedUser);

        // Act
        UserDTO response = userService.updatedUserPartially(userId, requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Old User", response.getUserName());
        assertEquals("updated@test.com", response.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getPaginatedUsers_ShouldReturnPaginatedUsersSuccessfully() {

        // Arrange
        int page = 0;
        int size = 2;

        User user1 = new User();
        user1.setId(1L);
        user1.setUserName("Priyakant");
        user1.setEmail("priya@test.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setUserName("Rahul");
        user2.setEmail("rahul@test.com");

        List<User> users = List.of(user1, user2);

        Pageable pageable = PageRequest.of(page, size);

        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // Act
        Page<UserDTO> response = userService.getPaginatedUsers(page, size);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getContent().size());

        assertEquals("Priyakant",
                response.getContent().get(0).getUserName());

        assertEquals("Rahul",
                response.getContent().get(1).getUserName());

        verify(userRepository, times(1))
                .findAll(pageable);
    }

    @Test
    void getPaginatedUsers_ShouldThrowException_WhenPageIsNegative() {

        // Arrange
        int page = -1;
        int size = 5;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getPaginatedUsers(page, size)
        );

        assertEquals("Invalid page or size", exception.getMessage());

        verify(userRepository, never()).findAll(any(Pageable.class));
    }
    @Test
    void getPaginatedUsers_ShouldThrowException_WhenSizeGreaterThan100() {

        // Arrange
        int page = 0;
        int size = 101;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getPaginatedUsers(page, size)
        );

        assertEquals("Invalid page or size", exception.getMessage());

        verify(userRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getUserSorted_ShouldReturnSortedUsersSuccessfully() {

        // Arrange
        int page = 0;
        int size = 2;
        String sortBy = "userName";

        User user1 = new User();
        user1.setId(1L);
        user1.setUserName("Aman");
        user1.setEmail("aman@test.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setUserName("Rahul");
        user2.setEmail("rahul@test.com");

        List<User> users = List.of(user1, user2);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy)
        );

        Page<User> userPage =
                new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable))
                .thenReturn(userPage);

        // Act
        Page<UserDTO> response =
                userService.getUserSorted(page, size, sortBy);

        // Assert
        assertNotNull(response);

        assertEquals(2,
                response.getContent().size());

        assertEquals("Aman",
                response.getContent().get(0).getUserName());

        assertEquals("Rahul",
                response.getContent().get(1).getUserName());

        verify(userRepository, times(1))
                .findAll(pageable);
    }

    @Test
    void getUserSorted_ShouldThrowException_WhenPageIsNegative() {

        // Arrange
        int page = -1;
        int size = 5;
        String sortBy = "userName";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUserSorted(page, size, sortBy)
        );

        assertEquals("Invalid page or size",
                exception.getMessage());

        verify(userRepository, never())
                .findAll(any(Pageable.class));
    }

    @Test
    void getUserSorted_ShouldThrowException_WhenSizeIsInvalid() {

        // Arrange
        int page = 0;
        int size = 0;
        String sortBy = "userName";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUserSorted(page, size, sortBy)
        );

        assertEquals("Invalid page or size",
                exception.getMessage());

        verify(userRepository, never())
                .findAll(any(Pageable.class));
    }

    @Test
    void getUserSorted_ShouldThrowException_WhenSizeGreaterThan100() {

        // Arrange
        int page = 0;
        int size = 101;
        String sortBy = "userName";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUserSorted(page, size, sortBy)
        );

        assertEquals("Invalid page or size",
                exception.getMessage());

        verify(userRepository, never())
                .findAll(any(Pageable.class));
    }

    @Test
    void searchUserByName_ShouldReturnUsersSuccessfully() {

        // Arrange
        String name = "Priya";

        User user1 = new User();
        user1.setId(1L);
        user1.setUserName("Priyakant");
        user1.setEmail("priya@test.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setUserName("Priyanshu");
        user2.setEmail("priyanshu@test.com");

        List<User> users = List.of(user1, user2);

        when(userRepository.findByUserNameContaining(name))
                .thenReturn(users);

        // Act
        List<UserDTO> response =
                userService.searchUserByName(name);

        // Assert
        assertNotNull(response);

        assertEquals(2, response.size());

        assertEquals("Priyakant",
                response.get(0).getUserName());

        assertEquals("Priyanshu",
                response.get(1).getUserName());

        verify(userRepository, times(1))
                .findByUserNameContaining(name);
    }

    @Test
    void searchUserByName_ShouldThrowException_WhenUsersNotFound() {

        // Arrange
        String name = "Unknown";

        when(userRepository.findByUserNameContaining(name))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.searchUserByName(name)
        );

        assertEquals(
                "No users found with name: Unknown",
                exception.getMessage()
        );

        verify(userRepository, times(1))
                .findByUserNameContaining(name);
    }
}