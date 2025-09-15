package com.tirana.smartparking.parking.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tirana.smartparking.common.dto.Money;
import com.tirana.smartparking.common.util.VisitSlice;
import com.tirana.smartparking.common.util.VisitSlicer;
import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;
import com.tirana.smartparking.parking.entity.LotRateAssignment;
import com.tirana.smartparking.parking.entity.RatePlan;
import com.tirana.smartparking.parking.entity.RateRule;
import com.tirana.smartparking.parking.entity.SpaceRateOverride;
import com.tirana.smartparking.parking.repository.LotRateAssignmentRepository;
import com.tirana.smartparking.parking.repository.RateRuleRepository;
import com.tirana.smartparking.parking.repository.SpaceRateOverrideRepository;
import com.tirana.smartparking.parking.service.ParkingService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PricingServiceImpl implements ParkingService {
  private final LotRateAssignmentRepository lotRatesRepository;
  private final SpaceRateOverrideRepository spaceRatesRepository;
  private final RateRuleRepository rulesRepository;

    public PricingServiceImpl(LotRateAssignmentRepository lotRatesRepository, SpaceRateOverrideRepository spaceRatesRepository, RateRuleRepository rulesRepository) {
        this.lotRatesRepository = lotRatesRepository;
        this.spaceRatesRepository = spaceRatesRepository;
        this.rulesRepository = rulesRepository;
    }


    public Money quote(Long lotId, Long spaceId, VehicleType vt, UserGroup ug,
                       ZonedDateTime start, ZonedDateTime end) {
    RatePlan plan = resolvePlan(lotId, spaceId, start, end);
    ZoneId zone = ZoneId.of(plan.getTimeZone());
    ZonedDateTime s = start.withZoneSameInstant(zone);
    ZonedDateTime e = end.withZoneSameInstant(zone);

    long minutes = Duration.between(s, e).toMinutes();
    if (plan.getGraceMinutes() != null && minutes <= plan.getGraceMinutes()) {
      return Money.zero(plan.getCurrency());
    }

    // Fetch rules once
    List<RateRule> planRules = rulesRepository.findByRatePlanId(plan.getId());

    // Split visit into day/time slices & compute
    int increment = Optional.ofNullable(plan.getIncrementMinutes()).orElse(1);
    int total = 0;
    Map<String,Integer> lines = new LinkedHashMap<>();

    for (VisitSlice slice : VisitSlicer.sliceByDayAndTime(s, e, planRules, zone)) {
      RateRule r = slice.matchedRule(vt, ug).orElse(null);
      if (r == null) continue;

      int billedMinutes = roundUp(slice.minutes(), increment);
      int sliceAmount = switch (plan.getType()) {
        case FLAT_PER_ENTRY -> Optional.ofNullable(r.getPriceFlat()).orElse(0);
        case PER_HOUR, TIME_OF_DAY, DAY_OF_WEEK -> perHour(billedMinutes, r.getPricePerHour());
        case TIERED -> applyTiers(slice.relativeMinutesFromStart(), billedMinutes, planRules);
        case FREE -> 0;
        case DYNAMIC -> dynamicPrice(slice, vt, ug); // your hook for occupancy-based pricing
      };

      String key = slice.label();
      lines.put(key, lines.getOrDefault(key, 0) + sliceAmount);
      total += sliceAmount;

      // optional: enforce daily caps by slice.getDay()
      if (plan.getDailyCap()!=null) {
        int daySpent = sumForDay(lines, slice.getDay());
        if (daySpent > plan.getDailyCap()) {
          int reduce = daySpent - plan.getDailyCap();
          total -= reduce;
          lines.put("Daily cap " + slice.getDay(), plan.getDailyCap());
        }
      }
    }

    // Return money (minor units)
    return new Money(plan.getCurrency(), total, toJson(lines));
  }

  // helpers...
  
  private RatePlan resolvePlan(Long lotId, Long spaceId, ZonedDateTime start, ZonedDateTime end) {
    // Check for space-specific overrides first (highest priority)
    if (spaceId != null) {
      List<SpaceRateOverride> spaceOverrides = spaceRatesRepository.findActiveOverridesForSpace(spaceId, start);
      if (!spaceOverrides.isEmpty()) {
        return spaceOverrides.getFirst().getRatePlan(); // Highest priority first
      }
    }
    
    // Fall back to lot-level assignments (only if lotId is provided)
    if (lotId != null) {
      List<LotRateAssignment> lotAssignments = lotRatesRepository.findActiveAssignmentsForLot(lotId, start);
      if (!lotAssignments.isEmpty()) {
        return lotAssignments.getFirst().getRatePlan(); // Highest priority first
      }
    }
    
    // If we have a space but no lot, and no space-specific rates, throw an error
    if (spaceId != null && lotId == null) {
      throw new RuntimeException("No rate plan found for standalone parking space " + spaceId + ". Please assign a rate plan to this space.");
    }
    
    throw new RuntimeException("No rate plan found for lot " + lotId + " and space " + spaceId);
  }
  
  private int roundUp(long minutes, int increment) {
    if (increment <= 0) return (int) minutes;
    return (int) ((minutes + increment - 1) / increment) * increment;
  }
  
  private int perHour(int minutes, Integer pricePerHour) {
    if (pricePerHour == null) return 0;
    return (int) Math.ceil((double) minutes / 60.0) * pricePerHour;
  }
  
  private int applyTiers(long relativeMinutesFromStart, int billedMinutes, List<RateRule> rules) {
    // Find applicable tier rules based on relative time from session start
    for (RateRule rule : rules) {
      if (rule.getStartMinute() != null && rule.getEndMinute() != null) {
        if (relativeMinutesFromStart >= rule.getStartMinute() && 
            relativeMinutesFromStart < rule.getEndMinute()) {
          if (rule.getPriceFlat() != null) {
            return rule.getPriceFlat();
          } else if (rule.getPricePerHour() != null) {
            return perHour(billedMinutes, rule.getPricePerHour());
          }
        }
      }
    }
    return 0;
  }
  
  private int dynamicPrice(VisitSlice slice, VehicleType vt, UserGroup ug) {
    // Placeholder for dynamic pricing logic
    // This could integrate with occupancy sensors, demand forecasting, etc.
    return 0;
  }
  
  private int sumForDay(Map<String, Integer> lines, java.time.LocalDate day) {
    return lines.entrySet().stream()
        .filter(entry -> entry.getKey().contains(day.toString()))
        .mapToInt(Map.Entry::getValue)
        .sum();
  }
  
  private String toJson(Map<String, Integer> lines) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(lines);
    } catch (JsonProcessingException e) {
      return "{}";
    }
  }
}
