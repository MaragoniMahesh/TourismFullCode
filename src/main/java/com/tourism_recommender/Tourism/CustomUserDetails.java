package com.tourism_recommender.Tourism;

import com.tourism_recommender.Tourism.Model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Return user authorities if needed
        return Collections.emptyList(); // Adjust as needed
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Or use user.getUsername() if preferred
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Adjust based on your requirements
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Adjust based on your requirements
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Adjust based on your requirements
    }

    @Override
    public boolean isEnabled() {
        return true; // Adjust based on your requirements
    }

    public User getUser() {
        return user;
    }
}

