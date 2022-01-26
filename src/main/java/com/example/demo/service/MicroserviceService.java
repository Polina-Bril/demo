package com.example.demo.service;

import com.example.demo.entity.ParkingMeterData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface MicroserviceService {
  void save(ParkingMeterData data);

  List<ParkingMeterData> find(UUID device, LocalDateTime from, LocalDateTime to, Boolean magneticLoopEnabled);

  Map<UUID, List<Double>> calculateStatistics(List<UUID> devices, LocalDateTime from, LocalDateTime to);
}
