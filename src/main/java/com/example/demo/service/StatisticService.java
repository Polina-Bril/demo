package com.example.demo.service;

import com.example.demo.entity.Statistic;

import java.util.List;
import java.util.UUID;

public interface StatisticService {

  List<Statistic> findStatistics(List<UUID> devices);
}
