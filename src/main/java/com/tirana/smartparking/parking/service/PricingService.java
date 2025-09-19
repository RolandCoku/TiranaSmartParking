package com.tirana.smartparking.parking.service;

import com.tirana.smartparking.common.dto.Money;
import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;

import java.time.ZonedDateTime;

public interface PricingService {
    Money quote(Long lotId, Long spaceId, VehicleType vt, UserGroup ug,
                ZonedDateTime start, ZonedDateTime end);
    
    Integer getGraceMinutes(Long lotId, Long spaceId, ZonedDateTime startTime);
}
