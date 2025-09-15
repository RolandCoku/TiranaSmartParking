package com.tirana.smartparking.parking.availability.controller;

import com.tirana.smartparking.parking.availability.AvailabilityStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/parking/lots")
class AvailabilityController {
  private final AvailabilityStream stream;
  public AvailabilityController(AvailabilityStream s) { this.stream = s; }

  @GetMapping("/{lotId}/availability/stream")
  public SseEmitter stream(@PathVariable Long lotId) {
    return stream.subscribe(lotId);
  }
}
