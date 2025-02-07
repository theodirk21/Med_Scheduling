package com.med.scheduling.service;

import com.med.scheduling.dto.MedsRequestDTO;
import com.med.scheduling.dto.MedsResponseDTO;
import com.med.scheduling.dto.MedsResponseIdDTO;
import com.med.scheduling.exception.NotFoundException;
import com.med.scheduling.models.ScheduleMed;
import com.med.scheduling.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MedServiceTest {

    @InjectMocks
    private MedService service;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Spy
    private ModelMapper mapper;

    private MedsRequestDTO medDTO;

    private ScheduleMed med;

    @BeforeEach
    void setup(){
        medDTO = MedsRequestDTO.builder()
                .medicationName("Name")
                .medicationTime(LocalTime.now())
                .medicationDay("seg")
                .chatId("chatID")
                .build();

        med = ScheduleMed.builder()
                .id(5L)
                .medicationName("Name")
                .medicationTime(LocalTime.now())
                .medicationDay("seg")
                .chatId("chatID")
                .build();
    }

    @Test
    void creatMed(){

        when(scheduleRepository.save(any())).thenReturn(med);

        MedsResponseIdDTO medId = service.createMed(medDTO);

        verify(scheduleRepository, times(1)).save(any(ScheduleMed.class));
        assertEquals(med.getId(), medId.getId());
    }

    @Test
    void updateMed(){

        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.ofNullable(med));

        med = ScheduleMed.builder()
                .id(5L)
                .medicationName("NameAtt")
                .medicationTime(LocalTime.now())
                .medicationDay("seg")
                .chatId("chatID")
                .build();

        medDTO = MedsRequestDTO.builder()
                .medicationName("NameAtt")
                .medicationTime(LocalTime.now())
                .medicationDay("seg")
                .chatId("chatID")
                .build();

        when(scheduleRepository.save(any(ScheduleMed.class))).thenReturn(med);

        MedsResponseDTO medsResponseDTO = service.updateMed(medDTO, 5L);

        verify(scheduleRepository, times(1)).findById(anyLong());
        verify(scheduleRepository, times(1)).save(any(ScheduleMed.class));
        assertEquals(medDTO.getMedicationName(), medsResponseDTO.getMedicationName());


    }

    @Test
    void nothingToList(){
        when(scheduleRepository.findAll()).thenReturn(Collections.emptyList()); // Simula nenhum medicamento encontrado

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            service.findAllMeds();
        });
    }
}
