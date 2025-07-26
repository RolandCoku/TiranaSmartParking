package com.tirana.smartparking.user.service.implementation;

import com.tirana.smartparking.common.exception.ResourceConflictException;
import com.tirana.smartparking.common.exception.ResourceNotFoundException;
import com.tirana.smartparking.user.dto.RoleDTO;
import com.tirana.smartparking.user.dto.UserCreateDTO;
import com.tirana.smartparking.user.dto.UserResponseDTO;
import com.tirana.smartparking.user.entity.User;
import com.tirana.smartparking.user.repository.RoleRepository;
import com.tirana.smartparking.user.repository.UserRepository;
import com.tirana.smartparking.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private static final String DEFAULT_ROLE = "USER";

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        // Fetch all users from the repository with pagination
        Page<User> userPage = userRepository.findAll(pageable);

        // Convert User entities to UserResponseDTOs
        return userPage.map(user -> new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getRoles().stream().map(role -> new RoleDTO(
                        role.getName(),
                        role.getDescription()
                )).collect(Collectors.toSet()),
                user.getCreatedAt(),
                user.getUpdatedAt()
        ));
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        // Fetch all users from the repository
        List<User> users = userRepository.findAll();

        // Convert User entities to UserResponseDTOs
        return users.stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhoneNumber(),
                        user.getRoles().stream().map(role -> new RoleDTO(
                                role.getName(),
                                role.getDescription()
                        )).collect(Collectors.toSet()),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                )).toList();
    }

    @Override
    public UserResponseDTO createUser(UserCreateDTO userCreateDTO) {
        // Validate the userCreateDTO object
        if (userCreateDTO == null || userCreateDTO.getEmail() == null || userCreateDTO.getPassword() == null) {
            throw new IllegalArgumentException("User creation failed: Invalid user data");
        }

        // Check if the user already exists
        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            throw new ResourceConflictException("User creation failed: User with this email already exists");
        }

        //Check if the passwords match
        if (!userCreateDTO.getPassword().equals(userCreateDTO.getConfirmPassword())) {
            throw new IllegalArgumentException("User creation failed: Passwords do not match");
        }

        User user = new User(
                userCreateDTO.getEmail(),
                userCreateDTO.getPassword(),
                userCreateDTO.getUsername(),
                userCreateDTO.getFirstName(),
                userCreateDTO.getLastName(),
                userCreateDTO.getPhoneNumber()
        );

        // Assign default role if no roles are provided
        if (userCreateDTO.getRoles() == null || userCreateDTO.getRoles().isEmpty()) {
            user.addRole(roleRepository.findByName(DEFAULT_ROLE).orElseThrow(() ->
                new ResourceNotFoundException("User creation failed: Default role '" + DEFAULT_ROLE + "' does not exist")));
        } else {
            for (String role : userCreateDTO.getRoles()) {
                user.addRole(roleRepository.findByName(role).orElseThrow(() ->
                    new ResourceNotFoundException("User creation failed: Role '" + role + "' does not exist")));
            }
        }

        // Save the user using the repository
        user = userRepository.save(user);


        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getRoles().stream().map(role -> new RoleDTO(
                        role.getName(),
                        role.getDescription()
                )).collect(Collectors.toSet()),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
