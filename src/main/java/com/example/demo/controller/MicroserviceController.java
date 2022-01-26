package com.example.demo.controller;

import com.example.demo.entity.ParkingMeterData;
import com.example.demo.entity.Statistic;
import com.example.demo.service.MicroserviceService;
import com.example.demo.service.StatisticService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class MicroserviceController {

  private static final Logger LOGGER = LogManager.getLogger(MicroserviceController.class);

  @Autowired
  private MicroserviceService microserviceService;
  @Autowired
  private StatisticService statisticService;

  @PostMapping("/save")
  public ResponseEntity<String> persistCollectedData(@RequestBody ParkingMeterData data) {
    LOGGER.info("New data received for saving!");
    microserviceService.save(data);
    return ResponseEntity.ok().body(HttpStatus.OK.getReasonPhrase());
  }

  @GetMapping("/reporter/get")
  public ResponseEntity<List<ParkingMeterData>> getData(@RequestParam(name = "device", required=false) UUID device, @RequestParam(name
      = "from", required=false) LocalDateTime from, @RequestParam(name = "to", required=false) LocalDateTime to, @RequestParam(name
      = "magneticLoopEnabled", required=false) Boolean magneticLoopEnabled) {
    LOGGER.info("New request received!");
    return ResponseEntity.ok().body(microserviceService.find(device, from, to, magneticLoopEnabled));
  }

  @GetMapping("/get-statistics")
  public ResponseEntity<Map<UUID, List<Double>>> getData(@RequestParam(name = "devices") List<UUID> devices,
                                                         @RequestParam(name = "from") LocalDateTime from,
                                                         @RequestParam(name = "to") LocalDateTime to) {
    LOGGER.info("New request for statistics received!");
    return ResponseEntity.ok().body(microserviceService.calculateStatistics(devices, from, to));
  }

  @GetMapping("/get-statistics-saved")
  public ResponseEntity<List<Statistic>> getData(@RequestParam(name = "devices") List<UUID> devices) {
    LOGGER.info("New request for saved statistics received!");
    return ResponseEntity.ok().body(statisticService.findStatistics(devices));
  }
}