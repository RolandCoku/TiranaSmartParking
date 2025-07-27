package com.tirana.smartparking.user.service.implementation;

import com.tirana.smartparking.common.exception.ResourceConflictException;
import com.tirana.smartparking.common.exception.ResourceNotFoundException;
import com.tirana.smartparking.user.dto.*;
import com.tirana.smartparking.user.entity.User;
import com.tirana.smartparking.user.repository.CarRepository;
import com.tirana.smartparking.user.repository.RoleRepository;
import com.tirana.smartparking.user.repository.UserRepository;
import com.tirana.smartparking.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CarRepository carRepository;
    private static final String DEFAULT_ROLE = "USER";

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           CarRepository carRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.carRepository = carRepository;
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
    public UserResponseDTO findById(Long id) {
        // Fetch user by ID from the repository
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Convert User entity to UserResponseDTO
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

    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        // Validate the userUpdateDTO object
        if (userUpdateDTO == null || userUpdateDTO.getEmail() == null) {
            throw new IllegalArgumentException("User update failed: Invalid user data");
        }

        // Fetch the existing user from the repository
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Update the user's fields
        existingUser.setUsername(userUpdateDTO.getUsername());
        existingUser.setEmail(userUpdateDTO.getEmail());
        existingUser.setFirstName(userUpdateDTO.getFirstName());
        existingUser.setLastName(userUpdateDTO.getLastName());
        existingUser.setPhoneNumber(userUpdateDTO.getPhoneNumber());

        // Save the updated user using the repository
        existingUser = userRepository.save(existingUser);

        return new UserResponseDTO(
                existingUser.getId(),
                existingUser.getUsername(),
                existingUser.getEmail(),
                existingUser.getFirstName(),
                existingUser.getLastName(),
                existingUser.getPhoneNumber(),
                existingUser.getRoles().stream().map(role -> new RoleDTO(
                        role.getName(),
                        role.getDescription()
                )).collect(Collectors.toSet()),
                existingUser.getCreatedAt(),
                existingUser.getUpdatedAt()
        );
    }

    @Override
    public UserResponseDTO patchUser(Long id, UserUpdateDTO userUpdateDTO) {
        // Validate the userUpdateDTO object
        if (userUpdateDTO == null) {
            throw new IllegalArgumentException("User patch failed: Invalid user data");
        }

        // Fetch the existing user from the repository
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Update only the fields that are provided in the patch request
        if (userUpdateDTO.getUsername() != null) {
            existingUser.setUsername(userUpdateDTO.getUsername());
        }
        if (userUpdateDTO.getEmail() != null) {
            // Check if the email is already in use by another user
            if (userRepository.existsByEmail(userUpdateDTO.getEmail()) && !existingUser.getEmail().equals(userUpdateDTO.getEmail())) {
                throw new ResourceConflictException("User patch failed: Email already in use");
            }
            existingUser.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getFirstName() != null) {
            existingUser.setFirstName(userUpdateDTO.getFirstName());
        }
        if (userUpdateDTO.getLastName() != null) {
            existingUser.setLastName(userUpdateDTO.getLastName());
        }
        if (userUpdateDTO.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(userUpdateDTO.getPhoneNumber());
        }

        // Save the updated user using the repository
        existingUser = userRepository.save(existingUser);

        return new UserResponseDTO(
                existingUser.getId(),
                existingUser.getUsername(),
                existingUser.getEmail(),
                existingUser.getFirstName(),
                existingUser.getLastName(),
                existingUser.getPhoneNumber(),
                existingUser.getRoles().stream().map(role -> new RoleDTO(
                        role.getName(),
                        role.getDescription()
                )).collect(Collectors.toSet()),
                existingUser.getCreatedAt(),
                existingUser.getUpdatedAt()
        );
    }

    @Override
    public void deleteUser(Long id) {
        // Fetch the user by ID from the repository
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        userRepository.delete(user);
    }

    @Override
    public Page<UserCarsDTO> getUserCars(Long userId, Pageable pageable) {
        // Fetch the user by ID from the repository
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Fetch the user's cars from the car repository
        return carRepository.findByUserId(userId, pageable)
                .map(car -> new UserCarsDTO(
                        car.getId(),
                        car.getLicensePlate(),
                        car.getBrand(),
                        car.getModel(),
                        car.getColor(),
                        car.getCreatedAt(),
                        car.getUpdatedAt()
                ));
    }

    @Override
    public UserResponseDTO addRoleToUser(Long userId, Set<String> roleNames) {
        // Fetch the user by ID from the repository
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Validate the role names
        if (roleNames == null || roleNames.isEmpty()) {
            throw new IllegalArgumentException("User update failed: Invalid role names");
        }

        // Add roles to the user
        for (String role : roleNames) {
            // Check if the role exists
            roleRepository.findByName(role.trim().toUpperCase()).ifPresentOrElse(
                user::addRole,
                () -> {
                    throw new ResourceNotFoundException("User update failed: Role '" + role + "' does not exist");
                }
            );
        }

        // Save the updated user using the repository
        user = userRepository.save(user);

        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getRoles().stream().map(r -> new RoleDTO(
                        r.getName(),
                        r.getDescription()
                )).collect(Collectors.toSet()),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Override
    public UserResponseDTO removeRoleFromUser(Long userId, String roleName) {
        // Validate the role name
        if (roleName == null || roleName.isEmpty()) {
            throw new IllegalArgumentException("User update failed: Invalid role name");
        }

        // Fetch the user by ID from the repository
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // We save the role name in uppercase in DB to ensure consistency so we normalize it
        String normalizedRoleName = roleName.trim().toUpperCase();

        // Check if the role exists
        roleRepository.findByName(normalizedRoleName).ifPresentOrElse(
                user::removeRole,
            () -> {
                throw new ResourceNotFoundException("User update failed: Role '" + roleName + "' does not exist");
            }
        );

        // Save the updated user using the repository
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

// Uncomment the following method if you want to fetch all users without pagination

//    @Override
//    public List<UserResponseDTO> getAllUsers() {
//        // Fetch all users from the repository
//        List<User> users = userRepository.findAll();
//
//        // Convert User entities to UserResponseDTOs
//        return users.stream()
//                .map(user -> new UserResponseDTO(
//                        user.getId(),
//                        user.getUsername(),
//                        user.getEmail(),
//                        user.getFirstName(),
//                        user.getLastName(),
//                        user.getPhoneNumber(),
//                        user.getRoles().stream().map(role -> new RoleDTO(
//                                role.getName(),
//                                role.getDescription()
//                        )).collect(Collectors.toSet()),
//                        user.getCreatedAt(),
//                        user.getUpdatedAt()
//                )).toList();
//    }

    // TODO: Add more validations and error handling as needed to createUser method
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
