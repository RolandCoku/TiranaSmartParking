package com.tirana.smartparking.parking.availability;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class AvailabilityStream {
  private final Map<Long, List<SseEmitter>> byLot = new HashMap<>();

  public SseEmitter subscribe(Long lotId) {
    var emitter = new SseEmitter(0L);
    byLot.computeIfAbsent(lotId, k -> new CopyOnWriteArrayList<>()).add(emitter);
    emitter.onCompletion(() -> byLot.getOrDefault(lotId, List.of()).remove(emitter));
    emitter.onTimeout(() -> byLot.getOrDefault(lotId, List.of()).remove(emitter));
    return emitter;
  }

  public void publish(Long lotId, long free) {
    var list = byLot.computeIfAbsent(lotId, k -> new CopyOnWriteArrayList<>());
    var dead = new ArrayList<SseEmitter>();
    for (var e : list) {
      try { e.send(SseEmitter.event().name("availability").data(free)); }
      catch (IOException ex) { dead.add(e); }
    }
    list.removeAll(dead);
  }
}
