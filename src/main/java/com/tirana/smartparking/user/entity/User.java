package com.tirana.smartparking.user.entity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Car> cars;

    public User() {
    }

    public User(String username, String password, String email, String phoneNumber, Set<Role> role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
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
        if (this.role == null) {
            this.role = new java.util.HashSet<>();
        }
        this.role.add(role);
    }

    public void addCar(Car car) {
        if (this.cars == null) {
            this.cars = new java.util.HashSet<>();
        }
        this.cars.add(car);
        car.setUser(this);
    }
}
