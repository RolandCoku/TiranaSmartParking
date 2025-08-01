package com.tirana.smartparking.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CarResponseDTO {
    @Setter(AccessLevel.NONE)
    private Long id;

    private String licensePlate;
    private String brand;
    private String model;
    private String color;

    @Setter(AccessLevel.NONE)
    private Long userId;

    private String userFirstName;
    private String userLastName;

    public CarResponseDTO() {
    }

    public CarResponseDTO(Long id, String licensePlate, String brand, String model, String color, Long userId, String userFirstName, String userLastName) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
    }

}
