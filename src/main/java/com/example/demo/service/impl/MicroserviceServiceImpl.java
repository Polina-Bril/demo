package com.example.demo.service.impl;

import com.example.demo.entity.ParkingMeterData;
import com.example.demo.entity.Statistic;
import com.example.demo.enums.ParkingMeterEvent;
import com.example.demo.repository.MicroserviceRepository;
import com.example.demo.repository.StatisticRepository;
import com.example.demo.service.MicroserviceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class MicroserviceServiceImpl implements MicroserviceService {

  private static final Logger LOGGER = LogManager.getLogger(MicroserviceServiceImpl.class);

  @Autowired
  private MicroserviceRepository microserviceRepository;
  @Autowired
  private StatisticRepository statisticRepository;

  @Override
  @Transactional
  public void save(ParkingMeterData data) {
    if (data.getEvent().equals(ParkingMeterEvent.PAYMENT_SUCCESS) || data.getEvent().equals(ParkingMeterEvent.MAGNETIC_LOOP_DISABLED)) {
      Optional<ParkingMeterData> lastData =
          microserviceRepository.findTopByDeviceAndEventOrderByTicketValidityEndDesc(data.getDevice(),
              ParkingMeterEvent.PAYMENT_SUCCESS);
      if (lastData.isPresent()) {
        long durationToNextPaymentSuccess = ChronoUnit.MILLIS.between(lastData.get().getTicketValidityEnd(),
            data.getTimestamp());
        data.setDurationFromLastSuccessPaymentValidityEnd(durationToNextPaymentSuccess);
      }
    }
    microserviceRepository.save(data);
    LOGGER.info(String.format("New data saved! Data: %s", data));
  }

  @Override
  public List<ParkingMeterData> find(UUID device, LocalDateTime from, LocalDateTime to, Boolean magneticLoopEnabled) {
    ArrayList<ParkingMeterData> returnData = new ArrayList<>();
    if (Objects.isNull(device)) {
      if (Objects.isNull(magneticLoopEnabled)) {
        returnData.addAll(microserviceRepository.findByTimestampBetween(from, to));
      } else if (Objects.isNull(from)) {
        returnData.addAll(microserviceRepository.findByMagneticLoopEnabled(magneticLoopEnabled));
      } else {
        returnData.addAll(microserviceRepository.findByTimestampBetweenAndMagneticLoopEnabled(from, to,
            magneticLoopEnabled));
      }
    } else {
      Optional<ParkingMeterData> data = Optional.empty();
      if (Objects.isNull(magneticLoopEnabled)) {
        if (Objects.isNull(from)) {
          returnData.addAll(microserviceRepository.findByDevice(device));
        } else {
          data = microserviceRepository.findByTimestampBetweenAndDevice(from, to, device);
        }
      } else {
        if (Objects.isNull(from)) {
          data = microserviceRepository.findByMagneticLoopEnabledAndDevice(magneticLoopEnabled, device);
        } else {
          data = microserviceRepository.findByTimestampBetweenAndMagneticLoopEnabledAndDevice(from, to,
              magneticLoopEnabled, device);
        }
      }
      data.ifPresent(returnData::add);
    }
    LOGGER.info(String.format("Data for request collected! Data: %s", returnData));
    return returnData;
  }


  @Override
  @Transactional
  public Map<UUID, List<Double>> calculateStatistics(List<UUID> devices, LocalDateTime from, LocalDateTime to) {
    HashMap<UUID, List<Double>> paidRates = new HashMap<>();
    long hours = ChronoUnit.HOURS.between(from, to);
    for (UUID device : devices) {
      List<Double> rates = new ArrayList<>();
      for (long i = 0; i < hours; i++) {
        LocalDateTime fromHour = from.plusHours(i);
        LocalDateTime toHour = from.plusHours(i + 1);
        long ticketValididty = 0;
        long timeLoopEnabled = 0;
        List<ParkingMeterData> a = microserviceRepository.findByTimestampBetweenAndDeviceAndEvent(fromHour, toHour,
            device, ParkingMeterEvent.PAYMENT_SUCCESS);
        List<ParkingMeterData> b = microserviceRepository.findByTicketValidityEndBetweenAndDeviceAndEvent(fromHour,
            toHour, device, ParkingMeterEvent.PAYMENT_SUCCESS);
        List<ParkingMeterData> c =
            microserviceRepository.findByTimestampBeforeAndTicketValidityEndAfterAndDeviceAndEvent(fromHour, toHour,
                device, ParkingMeterEvent.PAYMENT_SUCCESS);
        for (ParkingMeterData pmd : a) {
          LocalDateTime timeTo = pmd.getTicketValidityEnd().isAfter(toHour) ? toHour : pmd.getTicketValidityEnd();
          ticketValididty = ticketValididty + ChronoUnit.MILLIS.between(pmd.getTimestamp(), timeTo);
        }
        for (ParkingMeterData pmd : b) {
          if (pmd.getTimestamp().isBefore(fromHour)) {
            ticketValididty = ticketValididty + ChronoUnit.MILLIS.between(fromHour, pmd.getTicketValidityEnd());
          }
        }
        for (ParkingMeterData pmd : c) {
          ticketValididty = ticketValididty + ChronoUnit.MILLIS.between(fromHour, toHour);
        }
        List<ParkingMeterData> d = microserviceRepository.findByTimestampBetweenAndDeviceAndEvent(fromHour, toHour,
            device, ParkingMeterEvent.MAGNETIC_LOOP_ENABLED);
        List<ParkingMeterData> e = microserviceRepository.findByTimestampBetweenAndDeviceAndEvent(fromHour, toHour,
            device, ParkingMeterEvent.MAGNETIC_LOOP_DISABLED);
        d.addAll(e);
        if (d.isEmpty()) {
          timeLoopEnabled = microserviceRepository.findTopByTimestampBeforeAndDeviceOrderByTimestampDesc(fromHour,
              device).get().isMagneticLoopEnabled() ?
              timeLoopEnabled + ChronoUnit.MILLIS.between(from, toHour) : timeLoopEnabled;
        } else {
          d.sort(new Comparator<ParkingMeterData>() {
            @Override
            public int compare(ParkingMeterData o1, ParkingMeterData o2) {
              return (int) (o1.getId() - o2.getId());
            }
          });
          for (int j = 0; j < d.size(); j++) {
            if (d.get(j).getEvent().equals(ParkingMeterEvent.MAGNETIC_LOOP_ENABLED)) {
              if (j + 1 < d.size()) {
                timeLoopEnabled = timeLoopEnabled + ChronoUnit.MILLIS.between(d.get(j).getTimestamp(),
                    d.get(j + 1).getTimestamp());
              } else {
                timeLoopEnabled = timeLoopEnabled + ChronoUnit.MILLIS.between(d.get(j).getTimestamp(), toHour);
              }
            } else {
              if (j == 0) {
                timeLoopEnabled = timeLoopEnabled + ChronoUnit.MILLIS.between(fromHour, d.get(j).getTimestamp());
              }
            }
          }
        }
        if (ticketValididty != 0) {
          double paidRate = (double) timeLoopEnabled * 100 / ticketValididty;
          rates.add(paidRate);
        }
      }
      paidRates.put(device, rates);
    }
    return paidRates;
  }

  //the top of every hour of every day
  @Scheduled(cron = "0 0 * * * *")
  public void cronJobForStatistics() {
    LocalDateTime from = LocalDateTime.now();
    List<UUID> devices = microserviceRepository.findDistinctDevices();
    Map<UUID, List<Double>> rates = calculateStatistics(devices, from, from.plusHours(1));
    for (UUID device : rates.keySet()) {
      Statistic statistic = new Statistic();
      statistic.setDevice(device);
      statistic.setPaidRates(rates.get(device));
      statisticRepository.save(statistic);
    }
  }
}
