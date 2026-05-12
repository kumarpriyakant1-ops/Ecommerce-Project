package com.project.ecommerce.repository;
import com.project.ecommerce.entity.RefreshToken;
import com.project.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
    void deleteByExpiryDateBefore(LocalDateTime time);
    void deleteByUser(User user);
}
