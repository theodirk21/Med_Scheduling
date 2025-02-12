package com.med.scheduling.service;

import com.med.scheduling.dto.MedsFilterDTO;
import com.med.scheduling.dto.MedsRequestDTO;
import com.med.scheduling.dto.MedsResponseDTO;
import com.med.scheduling.dto.MedsResponseIdDTO;
import com.med.scheduling.exception.NotFoundControllerException;
import com.med.scheduling.models.ScheduleMed;
import com.med.scheduling.repository.ScheduleRepository;
import com.med.scheduling.repository.projection.CustomScheduleMed;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MedService {

    @Autowired
    private ScheduleRepository repository;

    @Autowired
    private ModelMapper mapper;


    @Transactional
    public MedsResponseIdDTO createMed(MedsRequestDTO medsRequestDTO) {

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

    @Transactional
    public MedsResponseDTO updateMed(MedsRequestDTO medsRequestDTO, Long id) {

        repository.findById(id).orElseThrow(() -> new NotFoundControllerException("Medicamento não encontrado"));

        ScheduleMed med = ScheduleMed.builder()
                .id(id)
                .medicationName(medsRequestDTO.getMedicationName())
                .medicationTime(medsRequestDTO.getMedicationTime())
                .medicationDay(medsRequestDTO.getMedicationDay())
                .chatId(medsRequestDTO.getChatId())
                .build();

        ScheduleMed scheduleMed = repository.save(med);

        return mapper.map(scheduleMed, MedsResponseDTO.class);
    }

    @Transactional
    public void deleteMed(Long id) {

        ScheduleMed scheduleMed = repository.findById(id).orElseThrow(() -> new NotFoundControllerException("Medicamento não encontrado"));

        repository.delete(scheduleMed);
    }

    public List<MedsResponseDTO> findAllMeds() {
        List<ScheduleMed> scheduleMeds = repository.findAll();

        if(scheduleMeds.isEmpty()) {
            throw new NotFoundControllerException("Medicamento não encontrado");
        } else {
        return scheduleMeds.stream()
                .map(scheduleMed -> mapper.map(scheduleMed, MedsResponseDTO.class))
                .toList();
        }
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

        return medsByFilter.stream()
                .map(filter -> mapper.map(filter, MedsResponseDTO.class))
                .toList();
    }
}
