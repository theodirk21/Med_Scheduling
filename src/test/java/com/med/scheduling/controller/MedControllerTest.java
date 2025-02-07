package com.med.scheduling.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.med.scheduling.MedSchedulingApplication;
import com.med.scheduling.dto.MedsFilterDTO;
import com.med.scheduling.dto.MedsRequestDTO;
import com.med.scheduling.dto.MedsResponseDTO;
import com.med.scheduling.dto.MedsResponseIdDTO;
import com.med.scheduling.exception.NotFoundException;
import com.med.scheduling.models.ScheduleMed;
import com.med.scheduling.repository.ScheduleRepository;
import com.med.scheduling.service.MedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class MedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ScheduleRepository repository;

    @MockBean
    private MedService service;

    private MedsResponseDTO medsResponseDTO;

    private MedsRequestDTO medsRequestDTO;

    @BeforeEach
    public void setup() {
        medsResponseDTO = MedsResponseDTO.builder()
                .chatId("testChatid")
                .medicationName("name")
                .medicationDay("segunda")
                .medicationTime(LocalTime.now())
                .build();

        medsRequestDTO = MedsRequestDTO.builder()
                .chatId("testChatid")
                .medicationName("name")
                .medicationDay("segunda")
                .medicationTime(LocalTime.now())
                .build();


    }

    @Test
    void findAllMeds() throws Exception {

        var listMed = List.of(medsResponseDTO, medsResponseDTO);
        when(service.findAllMeds()).thenReturn(listMed);

        mockMvc.perform(get("/")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(listMed)))
                .andReturn();

        verify(service, times(1)).findAllMeds();
        verify(repository, times(1)).findAll();
    }

    @Test
    void findMedsByFilter() throws Exception {

        var listMed = List.of(medsResponseDTO);
        when(service.findMedsByFilter(any(), any())).thenReturn(listMed);

        mockMvc.perform(get("/filter/")
                        .param("id", String.valueOf(1))
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(0)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(listMed)))
                .andReturn();

        verify(service, times(1)).findMedsByFilter(any(Pageable.class), any(MedsFilterDTO.class));

    }

    @Test
    void deleteMed() throws Exception {

        doNothing().when(service).deleteMed(anyLong());

        mockMvc.perform(delete("/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();

        verify(service, times(1)).deleteMed(anyLong());
    }

    @Test
    void createMed() throws Exception {

        when(service.createMed(medsRequestDTO)).thenReturn(MedsResponseIdDTO.builder().id(2L).build());

        ResultActions response = mockMvc.perform(post("/")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(medsRequestDTO)));

        response.andExpect(status().isCreated())
                .andDo(print());

        verify(service, times(1)).createMed(any(MedsRequestDTO.class));
    }

    @Test
    void updateMed() throws Exception {

        MedsResponseDTO updated = MedsResponseDTO.builder()
                .chatId("testChatidAtualizado")
                .medicationName("name")
                .medicationDay("segunda")
                .medicationTime(LocalTime.now())
                .build();

        MedsRequestDTO fortUpdate = MedsRequestDTO.builder()
                .chatId("testChatidAtualizado")
                .medicationName("name")
                .medicationDay("segunda")
                .medicationTime(LocalTime.now())
                .build();

        when(repository.findById(anyLong())).thenReturn(Optional.ofNullable(ScheduleMed
                .builder()
                .chatId("testChatid")
                .medicationName("name")
                .medicationDay("segunda")
                .medicationTime(LocalTime.now())
                .build()));

        when(service.updateMed(any(MedsRequestDTO.class), anyLong())).thenReturn(updated);

        mockMvc.perform(patch("/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fortUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chatId").value(updated.getChatId()))
                .andExpect(jsonPath("$.medicationName").value(updated.getMedicationName()))
                .andExpect(jsonPath("$.medicationDay").value(updated.getMedicationDay()))
                .andExpect(jsonPath("$.medicationTime").value(updated.getMedicationTime()))
                .andDo(print());

        verify(service, times(1)).updateMed(any(MedsRequestDTO.class), anyLong());

    }
}