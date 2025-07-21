package com.tirana.smartparking.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarCreateDTO {
    private String licensePlate;
    private String brand;
    private String model;
    private String color;

    public CarCreateDTO() {
    }

    public CarCreateDTO(String licensePlate, String brand, String model, String color) {
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.color = color;
    }
}
