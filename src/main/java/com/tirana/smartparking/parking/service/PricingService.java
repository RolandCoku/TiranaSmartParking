package com.tirana.smartparking.parking.service;

import com.tirana.smartparking.common.util.VisitSlicer;
import com.tirana.smartparking.parking.entity.Enum.UserGroup;
import com.tirana.smartparking.parking.entity.Enum.VehicleType;
import com.tirana.smartparking.parking.entity.RatePlan;
import com.tirana.smartparking.parking.entity.RateRule;
import com.tirana.smartparking.parking.repository.LotRateAssignmentRepository;
import com.tirana.smartparking.parking.repository.RateRuleRepository;
import com.tirana.smartparking.parking.repository.SpaceRateOverrideRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PricingService {
  private final LotRateAssignmentRepository lotRatesRepository;
  private final SpaceRateOverrideRepository spaceRatesRepository;
  private final RateRuleRepository rulesRepository;

    public PricingService(LotRateAssignmentRepository lotRatesRepository, SpaceRateOverrideRepository spaceRatesRepository, RateRuleRepository rulesRepository) {
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
    List<RateRule> planRules = rules.findByRatePlanId(plan.getId());

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
}
