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
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Log4j2
public class MedService {

    @Autowired
    private ScheduleRepository repository;

    @Autowired
    private ModelMapper mapper;


    @Transactional
    public MedsResponseIdDTO createMed(MedsRequestDTO medsRequestDTO) {


       validateDay(medsRequestDTO.getMedicationDay());

        ScheduleMed med = ScheduleMed.builder()
                .medicationName(medsRequestDTO.getMedicationName())
                .medicationTime(medsRequestDTO.getMedicationTime())
                .medicationDay(medsRequestDTO.getMedicationDay())
                .chatId(medsRequestDTO.getChatId())
                .build();

        ScheduleMed scheduleMed = repository.save(med);

        return MedsResponseIdDTO.builder()
                .id(scheduleMed.getId())
                .build();
    }

    private void validateDay(String medicationDay) {
        if (medicationDay != null && !medicationDay.trim().isEmpty()) {
            String regex = "\\b(domingo|segunda|terça|quarta|quinta|sexta|sábado)\\b";
            Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(medicationDay.toLowerCase());

            int count = 0;
            while (matcher.find()) {
                count++;
            }

            if (count == 0) {
                throw new ReminderDayException("O dia precisa ser um dia da semana como 'segunda' ou 'terça'.");
            } else if (count > 1) {
                throw new ReminderDayException("É aceito apenas um dia por registro.");
            }
        } else {
            throw new ReminderDayException("O dia da medicação não pode estar vazio.");
        }
    }

    @Transactional
    public MedsResponseDTO updateMed(MedsRequestDTO medsRequestDTO, Long id) {

        ScheduleMed med = repository.findById(id).orElseThrow(NotFoundException::new);

        if(medsRequestDTO.getMedicationDay() != null){
            validateDay(medsRequestDTO.getMedicationDay());
        }

        updateMedFields(medsRequestDTO, med);

        return mapper.map(repository.save(med), MedsResponseDTO.class);
    }

    private static void updateMedFields(MedsRequestDTO medsRequestDTO, ScheduleMed med) {
        if (medsRequestDTO.getMedicationName() != null) {
            med.setMedicationName(medsRequestDTO.getMedicationName());
        }
        if (medsRequestDTO.getMedicationTime() != null) {
            med.setMedicationTime(medsRequestDTO.getMedicationTime());
        }
        if (medsRequestDTO.getMedicationDay() != null) {
            med.setMedicationDay(medsRequestDTO.getMedicationDay());
        }
        if (medsRequestDTO.getChatId() != null) {
            med.setChatId(medsRequestDTO.getChatId());
        }
    }

    @Transactional
    public void deleteMed(Long id) {

        ScheduleMed scheduleMed = repository.findById(id).orElseThrow(NotFoundException::new);

        repository.delete(scheduleMed);
    }

    public List<MedsResponseDTO> findAllMeds() {
        List<ScheduleMed> scheduleMeds = repository.findAll();

        if (scheduleMeds.isEmpty()) throw new NotFoundException();


        return scheduleMeds.stream()
                .map(scheduleMed -> mapper.map(scheduleMed, MedsResponseDTO.class))
                .toList();

    }

    public List<MedsResponseDTO> findMedsByFilter(Pageable page, MedsFilterDTO paramsFilter) {
        List<CustomScheduleMed> medsByFilter = repository.findMedsByFilter(
                paramsFilter != null ? paramsFilter.id() : null,
                paramsFilter != null ? paramsFilter.chatId() : null,
                paramsFilter != null ? paramsFilter.medicationDay() : null,
                paramsFilter != null ? paramsFilter.medicationTime() : null,
                paramsFilter != null ? paramsFilter.medicationName() : null,
                page
        );

        if (medsByFilter.isEmpty()) throw new NotFoundException();

        return medsByFilter.stream()
                .map(filter -> mapper.map(filter, MedsResponseDTO.class))
                .toList();
    }
}
