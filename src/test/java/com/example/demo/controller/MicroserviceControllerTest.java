package com.example.demo.controller;

import com.example.demo.entity.ParkingMeterData;
import com.example.demo.enums.ParkingMeterEvent;
import com.example.demo.enums.PaymentMode;
import com.example.demo.service.MicroserviceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
class MicroserviceControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private MicroserviceService microserviceService;

  @AfterEach
  void tearDown() {
  }

  @Test
  void testGetData() throws Exception {
    // Given
    ParkingMeterData data = new ParkingMeterData();
    data.setDevice(UUID.fromString("0d20c98d-6671-4981-a01b-51b768aa4b83"));
    data.setTimestamp(LocalDateTime.now());
    data.setEvent(ParkingMeterEvent.PAYMENT_SUCCESS);
    data.setSpaceUse(70);
    data.setCenter(14);
    data.setMagneticLoopEnabled(true);
    data.setTicketValidityEnd(LocalDateTime.now().plusHours(1));
    data.setPrice(0.90);
    data.setPaymentMode(PaymentMode.CASH);
    data.setFailures(List.of(123, 8366));
    microserviceService.save(data);

    // When
    ResultActions resultActions = mockMvc.perform(get("/reporter/get?device={device}", data.getDevice()))
        .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}"));

    // Then
    resultActions.andExpect(status().isOk());
  }
}