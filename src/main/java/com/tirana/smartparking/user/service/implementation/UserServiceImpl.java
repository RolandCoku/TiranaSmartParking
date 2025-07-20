package com.tirana.smartparking.user.service.implementation;

import com.tirana.smartparking.user.dto.UserCreateDTO;
import com.tirana.smartparking.user.dto.UserResponseDTO;
import com.tirana.smartparking.user.entity.User;
import com.tirana.smartparking.user.repository.RoleRepository;
import com.tirana.smartparking.user.repository.UserRepository;
import com.tirana.smartparking.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private static final String DEFAULT_ROLE = "USER";

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    @Override
    public UserResponseDTO createUser(UserCreateDTO userCreateDTO) {
        // Validate the userCreateDTO object
        if (userCreateDTO == null || userCreateDTO.getEmail() == null || userCreateDTO.getPassword() == null) {
            throw new IllegalArgumentException("User creation failed: Invalid user data");
        }

        User user = new User(
                userCreateDTO.getEmail(),
                userCreateDTO.getPassword(),
                userCreateDTO.getUsername(),
                userCreateDTO.getFirstName(),
                userCreateDTO.getLastName(),
                userCreateDTO.getPhoneNumber()
        );

        // Save the user using the repository
        user = userRepository.save(user);

        //TODO: Handle role assignment

        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber()
        );
    }
}
