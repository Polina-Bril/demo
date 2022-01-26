package com.example.demo.repository;

import com.example.demo.entity.Statistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StatisticRepository extends JpaRepository<Statistic, Long> {

  Statistic findTopByDeviceOrderByIdDesc(UUID device);
}
