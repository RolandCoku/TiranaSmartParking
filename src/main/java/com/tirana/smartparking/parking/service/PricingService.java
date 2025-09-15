package com.tirana.smartparking.parking.service;

import com.tirana.smartparking.common.dto.Money;
import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
@Service
public interface PricingService {
    public Money quote(Long lotId, Long spaceId, VehicleType vt, UserGroup ug,
                       ZonedDateTime start, ZonedDateTime end);
}
