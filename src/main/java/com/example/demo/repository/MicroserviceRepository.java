package com.example.demo.repository;

import com.example.demo.entity.ParkingMeterData;
import com.example.demo.enums.ParkingMeterEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MicroserviceRepository extends JpaRepository<ParkingMeterData, Long> {

  List<ParkingMeterData> findByDevice(UUID device);

  List<ParkingMeterData> findByMagneticLoopEnabled(boolean magneticLoopEnabled);

  List<ParkingMeterData> findByTimestampBetween(LocalDateTime from, LocalDateTime to);

  List<ParkingMeterData> findByTimestampBetweenAndMagneticLoopEnabled(LocalDateTime from, LocalDateTime to,
                                                                      boolean magneticLoopEnabled);

  Optional<ParkingMeterData> findByTimestampBetweenAndMagneticLoopEnabledAndDevice(LocalDateTime from,
                                                                                   LocalDateTime to,
                                                                                   boolean magneticLoopEnabled,
                                                                                   UUID device);

  Optional<ParkingMeterData> findByTimestampBetweenAndDevice(LocalDateTime from, LocalDateTime to, UUID device);

  List<ParkingMeterData> findByTimestampBetweenAndDeviceAndEvent(LocalDateTime from, LocalDateTime to, UUID device,
                                                                 ParkingMeterEvent event);

  List<ParkingMeterData> findByTicketValidityEndBetweenAndDeviceAndEvent(LocalDateTime from, LocalDateTime to,
                                                                         UUID device, ParkingMeterEvent event);

  List<ParkingMeterData> findByTimestampBeforeAndTicketValidityEndAfterAndDeviceAndEvent(LocalDateTime from,
                                                                                         LocalDateTime to,
                                                                                         UUID device,
                                                                                         ParkingMeterEvent event);

  Optional<ParkingMeterData> findTopByTimestampBeforeAndDeviceOrderByTimestampDesc(LocalDateTime from, UUID device);

  Optional<ParkingMeterData> findByMagneticLoopEnabledAndDevice(boolean magneticLoopEnabled, UUID device);

  Optional<ParkingMeterData> findTopByDeviceAndEventOrderByTicketValidityEndDesc(UUID device,
                                                                                 ParkingMeterEvent event);

  @Query("select distinct a.device from ParkingMeterData a")
  List<UUID> findDistinctDevices();
}
