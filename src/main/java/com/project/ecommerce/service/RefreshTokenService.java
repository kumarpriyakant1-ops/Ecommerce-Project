package com.project.ecommerce.service;

import com.project.ecommerce.dto.LoginResponseDTO;
import com.project.ecommerce.entity.RefreshToken;
import com.project.ecommerce.entity.User;
import com.project.ecommerce.repository.RefreshTokenRepository;
import com.project.ecommerce.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final JwtService jwtService;

    private final RefreshTokenRepository refreshTokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

    public RefreshTokenService(JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(User user){
        logger.info("Creating refresh Token");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        logger.info("Successfully created the refresh token");
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public LoginResponseDTO rotateToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow( () -> {
                    logger.error("Invalid refresh token");
                    return new RuntimeException("Invalid refresh token");
                });

        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            logger.error("Refresh token expired");
            throw new RuntimeException("Refresh token expired");
        }

        refreshTokenRepository.deleteByToken(refreshTokenValue);

        User user = refreshToken.getUser();

//        String accessToken = jwtService.generateToken(user.getEmail());
        String accessToken = jwtService.generateToken(user);
        RefreshToken newRefreshToken = createRefreshToken(user);

        return new LoginResponseDTO(accessToken, newRefreshToken.getToken());
    }


    public String  generateAccessToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow( () -> {
                    logger.error("Invalid refresh token");
                    return new RuntimeException("Invalid refresh token");
                });

        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            logger.error("Refresh token expired");
            throw new RuntimeException("Refresh token expired");
        }
//        return jwtService.generateToken(refreshToken.getUser().getEmail());
        return jwtService.generateToken(refreshToken.getUser());

    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
        logger.info("Refresh token deleted successfully");
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredTokens(){
        refreshTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        logger.info("Expired refresh tokens deleted");
    }


}
