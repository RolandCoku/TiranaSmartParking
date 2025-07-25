package com.tirana.smartparking.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCarsDTO {
    @Setter(AccessLevel.NONE)
    private Long id;
    private String licensePlate;
    private String brand;
    private String model;
    private String color;

    public UserCarsDTO() {
    }

    public UserCarsDTO(Long id, String licensePlate, String brand, String model, String color) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.color = color;
    }
}
