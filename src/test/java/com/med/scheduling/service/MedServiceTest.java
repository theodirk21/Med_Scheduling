package com.med.scheduling.service;

import com.med.scheduling.dto.MedsFilterDTO;
import com.med.scheduling.dto.MedsRequestDTO;
import com.med.scheduling.dto.MedsResponseDTO;
import com.med.scheduling.dto.MedsResponseIdDTO;
import com.med.scheduling.exception.NotFoundException;
import com.med.scheduling.exception.ReminderDayException;
import com.med.scheduling.models.ScheduleMed;
import com.med.scheduling.repository.ScheduleRepository;
import com.med.scheduling.repository.projection.CustomScheduleMed;
import org.assertj.core.condition.Not;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
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

    @Mock
    private CustomScheduleMed customScheduleMed;

    private MedsRequestDTO medDTO;

    private ScheduleMed med;

    @BeforeEach
    void setup(){
        medDTO = MedsRequestDTO.builder()
                .medicationName("Name")
                .medicationTime(LocalTime.now())
                .medicationDay("segunda")
                .chatId("chatID")
                .build();

        med = ScheduleMed.builder()
                .id(5L)
                .medicationName("Name")
                .medicationTime(LocalTime.now())
                .medicationDay("segunda")
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
    void creatMed_moretThanOneDay(){

        medDTO = MedsRequestDTO.builder()
                .medicationName("Name")
                .medicationTime(LocalTime.now())
                .medicationDay("segunda, terça")
                .chatId("chatID")
                .build();

        ReminderDayException ex = assertThrows(ReminderDayException.class, () -> service.createMed(medDTO));
        assertEquals("É aceito apenas um dia por registro.", ex.getMessage());
    }

    @Test
    void creatMed_DayWrongName(){

        medDTO = MedsRequestDTO.builder()
                .medicationName("Name")
                .medicationTime(LocalTime.now())
                .medicationDay("seg")
                .chatId("chatID")
                .build();

        ReminderDayException ex = assertThrows(ReminderDayException.class, () -> service.createMed(medDTO));
        assertEquals("O dia precisa ser um dia da semana como 'segunda' ou 'terça'.", ex.getMessage());
    }

    @Test
    void creatMed_DayEmpty(){

        medDTO = MedsRequestDTO.builder()
                .medicationName("Name")
                .medicationTime(LocalTime.now())
                .medicationDay("")
                .chatId("chatID")
                .build();

        ReminderDayException ex = assertThrows(ReminderDayException.class, () -> service.createMed(medDTO));
        assertEquals("O dia da medicação não pode estar vazio.", ex.getMessage());
    }

    @Test
    void updateMed_moretThanOneDay(){

        medDTO = MedsRequestDTO.builder()
                .medicationName("NameAtt")
                .medicationTime(LocalTime.now())
                .medicationDay("segunda, terça")
                .chatId("chatID")
                .build();

        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.ofNullable(med));
        ReminderDayException ex = assertThrows(ReminderDayException.class, () -> service.updateMed(medDTO, 5L));
        assertEquals("É aceito apenas um dia por registro.", ex.getMessage());
    }

    @Test
    void updateMed_DayWrongName(){

        medDTO = MedsRequestDTO.builder()
                .medicationName("NameAtt")
                .medicationTime(LocalTime.now())
                .medicationDay("seg")
                .chatId("chatID")
                .build();

        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.ofNullable(med));
        ReminderDayException ex = assertThrows(ReminderDayException.class, () -> service.updateMed(medDTO, 5L));
        assertEquals("O dia precisa ser um dia da semana como 'segunda' ou 'terça'.", ex.getMessage());
    }

    @Test
    void updateMed_DayEmpty(){

        medDTO = MedsRequestDTO.builder()
                .medicationName("NameAtt")
                .medicationTime(LocalTime.now())
                .medicationDay("")
                .chatId("chatID")
                .build();

        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.ofNullable(med));
        ReminderDayException ex = assertThrows(ReminderDayException.class, () -> service.updateMed(medDTO, 5L));
        assertEquals("O dia da medicação não pode estar vazio.", ex.getMessage());
    }

    @Test
    void updateMed_NotFoundReference(){

        medDTO = MedsRequestDTO.builder()
                .medicationName("NameAtt")
                .medicationTime(LocalTime.now())
                .medicationDay("seg")
                .chatId("chatID")
                .build();


        assertThrows(NotFoundException.class, () -> service.updateMed(medDTO, 5L));

    }



    @Test
    void updateMed(){

        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.ofNullable(med));

        med = ScheduleMed.builder()
                .id(5L)
                .medicationName("NameAtt")
                .medicationTime(LocalTime.now())
                .medicationDay("segunda")
                .chatId("chatID")
                .build();

        medDTO = MedsRequestDTO.builder()
                .medicationName("NameAtt")
                .medicationTime(LocalTime.now())
                .medicationDay("segunda")
                .chatId("chatID")
                .build();

        when(scheduleRepository.save(any(ScheduleMed.class))).thenReturn(med);

        MedsResponseDTO medsResponseDTO = service.updateMed(medDTO, 5L);

        verify(scheduleRepository, times(1)).findById(anyLong());
        verify(scheduleRepository, times(1)).save(any(ScheduleMed.class));
        assertEquals(medDTO.getMedicationName(), medsResponseDTO.getMedicationName());


    }

    @Test
    void updateMed_JustOneInfo(){

        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.ofNullable(med));

        med = ScheduleMed.builder()
                .id(5L)
                .medicationName("NameAtt")
                .medicationTime(LocalTime.now())
                .medicationDay("segunda")
                .chatId("chatID")
                .build();

        medDTO = MedsRequestDTO.builder()
                .medicationName("NameAtt")
                        .build();

        when(scheduleRepository.save(any(ScheduleMed.class))).thenReturn(med);

        MedsResponseDTO medsResponseDTO = service.updateMed(medDTO, 5L);

        verify(scheduleRepository, times(1)).findById(anyLong());
        verify(scheduleRepository, times(1)).save(any(ScheduleMed.class));
        assertEquals(medDTO.getMedicationName(), medsResponseDTO.getMedicationName());
    }

    @Test
    void delete(){
        when(scheduleRepository.findById(anyLong())).thenReturn(Optional.ofNullable(med));
        doNothing().when(scheduleRepository).delete(any(ScheduleMed.class));

        service.deleteMed(1L);

        verify(scheduleRepository, times(1)).delete(any());
    }

    @Test
    void delete_NotFound(){
        when(scheduleRepository.findById(anyLong())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> service.deleteMed(anyLong()));
    }

    @Test
    void findAllMeds(){
        when(scheduleRepository.findAll()).thenReturn(List.of(med, med, med));

        List<MedsResponseDTO> allMeds = service.findAllMeds();

        verify(scheduleRepository, times(1)).findAll();
        assertNotNull(allMeds);
        assertEquals(med.getId(), allMeds.get(0).getId());
        assertEquals(med.getChatId(), allMeds.get(0).getChatId());
        assertEquals(med.getMedicationDay(), allMeds.get(0).getMedicationDay());
        assertEquals(med.getMedicationTime(), allMeds.get(0).getMedicationTime());
        assertEquals(med.getMedicationName(), allMeds.get(0).getMedicationName());
    }

    @Test
    void nothingToList(){
        when(scheduleRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> {
            service.findAllMeds();
        });
    }

    @Test
    void findMedsByFilterByNameDayAndWithNullParams(){

        var paramsFilter = new MedsFilterDTO(null, null, "seg", null, "teste");
        var medsResponseDTO = MedsResponseDTO.builder()
                .medicationName("teste")
                .medicationDay("seg")
                .build();

        when(scheduleRepository
                .findMedsByFilter(any(), any(), any(), any(), any(),any()))
                .thenReturn(List.of(customScheduleMed));
        when(mapper.map(customScheduleMed, MedsResponseDTO.class)).thenReturn(medsResponseDTO);
        List<MedsResponseDTO> medsByFilter = service.findMedsByFilter(Pageable.ofSize(1), paramsFilter);

        verify(scheduleRepository, times(1)).findMedsByFilter(any(),any(),any(),any(),any(),any(Pageable.class));
        assertNotNull(medsByFilter);
        assertEquals(medsResponseDTO.getMedicationName(), medsByFilter.get(0).getMedicationName());
        assertEquals(medsResponseDTO.getMedicationDay(), medsByFilter.get(0).getMedicationDay());

    }

    @Test
    void testFindMedsByFilter_NoResults_ReturnsEmptyList() {
        var paramsFilter = new MedsFilterDTO(null, null, "seg", null, "teste");

        when(scheduleRepository.findMedsByFilter(isNull(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(List.of());

        assertThrows(NotFoundException.class, () -> service.findMedsByFilter(Pageable.ofSize(1), paramsFilter));
    }
}
