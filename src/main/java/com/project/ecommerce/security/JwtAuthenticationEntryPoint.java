package com.project.ecommerce.security;

import com.project.ecommerce.dto.ErrorResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence( HttpServletRequest request,
                          HttpServletResponse response,
                          AuthenticationException authExceptio)
                          throws IOException, ServletException{
        ErrorResponseDTO error = new ErrorResponseDTO(
                "AUTH_401",
                List.of("Authorization token is missing or invalid"),
                HttpStatus.UNAUTHORIZED.value(),
                LocalDateTime.now()
        );
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), error);

    }
}
