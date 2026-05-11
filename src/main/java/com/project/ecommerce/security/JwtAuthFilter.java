package com.project.ecommerce.security;

import com.project.ecommerce.entity.User;
import com.project.ecommerce.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // CHECK TOKEN EXISTS
        String authHeader = request.getHeader("Authorization");

        // REMOVE "Bearer"
        if(authHeader != null && authHeader.startsWith("Bearer")){

            String token = authHeader.substring(7);
            String email = jwtService.extractEmail(token);

            // FETCH USER FROM DATABASE
            User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("User not found"));

            // CREATE ROLE
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );

            // CREATE AUTH OBJECT
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            authorities);
                            //Collections.emptyList());

            // SET AUTHENTICATION
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(auth);
        }

        filterChain.doFilter(
                request,
                response);
    }
}
