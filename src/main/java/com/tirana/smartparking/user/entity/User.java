package com.tirana.smartparking.user.entity;
import com.tirana.smartparking.common.exception.ResourceNotFoundException;
import com.tirana.smartparking.common.exception.RoleOperationNotAllowedException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;
    private String password;

    @Column(nullable = false, unique = true)
    private String email;
    private String phoneNumber;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Car> cars = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(String firstName, String lastName, String username, String password, String email, String phoneNumber, Set<Role> roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
    }

    public User(String email, String password, String username, String firstName, String lastName, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public void addRole(Role role) {
        if (this.roles == null) {
            this.roles = new java.util.HashSet<>();
        }
        this.roles.add(role);
    }

    public void removeRole(Role role) throws ResourceNotFoundException, RoleOperationNotAllowedException {
        if (this.roles == null || !this.roles.contains(role)) {
            throw new ResourceNotFoundException("Role " + role.getName() + " not found in user's roles");
        }

        if (this.roles.size() == 1) {
            throw new RoleOperationNotAllowedException(
                    "Cannot remove the last role from user " + this.username + ". At least one role is required.");
        }

        this.roles.remove(role);
    }

    public void addCar(Car car) {
        if (this.cars == null) {
            this.cars = new java.util.HashSet<>();
        }
        this.cars.add(car);
        car.setUser(this);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Collect all permissions from all roles and convert them to SimpleGrantedAuthority.
        // Also include roles themselves as authorities (e.g., "ROLE_USER") if you want to use hasRole()
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Add permissions
        if (this.roles != null) {
            this.roles.forEach(role -> {
                if (role.getPermissions() != null) {
                    role.getPermissions().forEach(permission ->
                            authorities.add(new SimpleGrantedAuthority(permission.getName().name()))
                    );
                }
                // Add the role name itself as an authority, prefixed with "ROLE_"
                // This allows using hasRole('ADMIN') or hasAuthority('ROLE_ADMIN')
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            });
        }
        return authorities;
    }
}
