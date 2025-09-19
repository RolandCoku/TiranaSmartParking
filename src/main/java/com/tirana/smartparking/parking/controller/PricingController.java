package com.tirana.smartparking.parking.controller;

import com.tirana.smartparking.common.dto.ApiResponse;
import com.tirana.smartparking.common.dto.Money;
import com.tirana.smartparking.common.response.ResponseHelper;
import com.tirana.smartparking.parking.dto.PricingQuoteDTO;
import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;
import com.tirana.smartparking.parking.service.RateManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/v1/pricing")
public class PricingController {
    
    private final RateManagementService rateManagementService;
    
    public PricingController(RateManagementService rateManagementService) {
        this.rateManagementService = rateManagementService;
    }
    
    @PreAuthorize("hasAuthority('PRICING_QUOTE')")
    @PostMapping("/quote")
    public ResponseEntity<ApiResponse<Money>> getPricingQuote(@Validated @RequestBody PricingQuoteDTO quoteDTO) {
        Money quote = rateManagementService.getPricingQuote(quoteDTO);
        return ResponseHelper.ok("Pricing quote calculated successfully", quote);
    }
    
    @PreAuthorize("hasAuthority('PRICING_QUOTE')")
    @GetMapping("/spaces/{spaceId}/quote")
    public ResponseEntity<ApiResponse<Money>> getStandaloneSpaceQuote(
            @PathVariable Long spaceId,
            @RequestParam VehicleType vehicleType,
            @RequestParam UserGroup userGroup,
            @RequestParam ZonedDateTime startTime,
            @RequestParam ZonedDateTime endTime) {
        Money quote = rateManagementService.getStandaloneSpacePricingQuote(spaceId, vehicleType, userGroup, startTime, endTime);
        return ResponseHelper.ok("Standalone space pricing quote calculated successfully", quote);
    }
}
