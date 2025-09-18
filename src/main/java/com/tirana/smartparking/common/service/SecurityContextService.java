package com.tirana.smartparking.common.service;

import com.tirana.smartparking.common.exception.ResourceNotFoundException;
import com.tirana.smartparking.user.entity.User;
import com.tirana.smartparking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityContextService {
    
    private final UserRepository userRepository;
    
    /**
     * Gets the current authenticated user's ID from the SecurityContext
     * @return the user ID
     * @throws ResourceNotFoundException if the user is not authenticated or not found
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("User not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof User user) {
            return user.getId();
        }
        
        // If principal is not a User entity, try to get by username
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        
        return user.getId();
    }
    
    /**
     * Gets the current authenticated user entity from the SecurityContext
     * @return the User entity
     * @throws ResourceNotFoundException if the user is not authenticated or not found
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadCredentialsException("User not authenticated");
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof User user) {
            return user;
        }
        
        // If principal is not User entity, try to get by username
        String username = authentication.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }
    
    /**
     * Gets the current authenticated user's email from the SecurityContext
     * @return the user email
     * @throws RuntimeException if the user is not authenticated
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        
        return authentication.getName();
    }
    
    /**
     * Checks if the current user has admin privileges
     * @return true if the user is an admin, false otherwise
     */
    public boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
    
    /**
     * Checks if the current user has a specific role
     * @param roleName the role to check for
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(roleName));
    }
    
    /**
     * Checks if the current user has a specific permission
     * @param permission the permission to check for
     * @return true if the user has the permission, false otherwise
     */
    public boolean hasPermission(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(permission));
    }
}
