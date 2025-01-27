package com.med.scheduling.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.med.scheduling.models.ScheduleMed;
import com.med.scheduling.service.MedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;


@WebMvcTest
class MedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;


    private MedService service;

    private ScheduleMed med;

    @BeforeEach
    public void setup() {
        // Given / Arrange
        med = ScheduleMed.builder()
                .chatId("testChatid")
                .medicationName("name")
                .medicationDay("segunda")
                .medicationTime(LocalTime.now())
                .build();

    }

    @Test
    void findAllMeds() {

        given


    }

    @Test
    void findMedsByFilter() {
    }

    @Test
    void deleteMed() {
    }

    @Test
    void createMed() {
    }

    @Test
    void updateMed() {
    }
}