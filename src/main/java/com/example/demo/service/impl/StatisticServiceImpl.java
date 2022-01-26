package com.example.demo.service.impl;

import com.example.demo.entity.Statistic;
import com.example.demo.repository.StatisticRepository;
import com.example.demo.service.StatisticService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StatisticServiceImpl implements StatisticService {

  private static final Logger LOGGER = LogManager.getLogger(StatisticServiceImpl.class);

  @Autowired
  private StatisticRepository statisticRepository;

  @Override
  public List<Statistic> findStatistics(List<UUID> devices) {
    List<Statistic> statistics = new ArrayList<>();
    for (UUID device : devices) {
      statistics.add(statisticRepository.findTopByDeviceOrderByIdDesc(device));
    }
    return statistics;
  }
}
