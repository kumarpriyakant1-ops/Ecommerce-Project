package com.project.ecommerce.security;

import com.project.ecommerce.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private User user;

    @Override
    public Collection<? extends SimpleGrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority(
                user.getRole().name()
        ));
    }

    @Override
    public String getUsername(){
        return user.getEmail();
    }

    @Override
    public String getPassword(){
        return user.getPassword();
    }
    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    @Override
    public boolean isEnabled() {

        return true;
    }


}
