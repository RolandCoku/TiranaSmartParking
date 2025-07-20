package com.tirana.smartparking.user.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCarsDTO {
    @Setter(AccessLevel.NONE)
    private Long id;
    private String carNumber;
    private String carModel;
    private String carColor;

    public UserCarsDTO() {
    }

    public UserCarsDTO(Long id, String carNumber, String carModel, String carColor) {
        this.id = id;
        this.carNumber = carNumber;
        this.carModel = carModel;
        this.carColor = carColor;
    }

}
