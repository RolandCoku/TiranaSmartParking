package com.tirana.smartparking.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CarResponseDTO {
    @Setter(AccessLevel.NONE)
    private Long id;

    private String carNumber;
    private String carModel;
    private String carColor;

    @Setter(AccessLevel.NONE)
    private Long userId;

    private String userName;

    public CarResponseDTO() {
    }

    public CarResponseDTO(Long id, String carNumber, String carModel, String carColor, Long userId, String userName) {
        this.id = id;
        this.carNumber = carNumber;
        this.carModel = carModel;
        this.carColor = carColor;
        this.userId = userId;
        this.userName = userName;
    }

}
