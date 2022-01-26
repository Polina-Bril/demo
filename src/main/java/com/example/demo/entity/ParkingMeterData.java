package com.example.demo.entity;

import com.example.demo.enums.ParkingMeterEvent;
import com.example.demo.enums.PaymentMode;
import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class ParkingMeterData {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  @NotNull
  private UUID device;
  @NotNull
  private LocalDateTime timestamp;
  @NotNull
  @Enumerated(EnumType.STRING)
  private ParkingMeterEvent event;
  @NotNull
  private int spaceUse;
  @NotNull
  private int center;
  @NotNull
  private boolean magneticLoopEnabled;
  private LocalDateTime ticketValidityEnd;
  private Double price;
  @Enumerated(EnumType.STRING)
  private PaymentMode paymentMode;
  @ElementCollection
  private List<Integer> failures;
  private long durationFromLastSuccessPaymentValidityEnd;
}
