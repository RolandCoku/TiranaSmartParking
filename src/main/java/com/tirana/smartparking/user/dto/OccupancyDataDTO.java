package com.tirana.smartparking.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OccupancyDataDTO {
    private Long lotId;
    private String lotName;
    private int totalSpaces;
    private int occupiedSpaces;
    private int availableSpaces;
    private double occupancyRate;
}
