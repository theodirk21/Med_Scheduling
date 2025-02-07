package com.med.scheduling.service;

import com.med.scheduling.dto.MedsFilterDTO;
import com.med.scheduling.dto.MedsRequestDTO;
import com.med.scheduling.dto.MedsResponseDTO;
import com.med.scheduling.dto.MedsResponseIdDTO;
import com.med.scheduling.exception.NotFoundException;
import com.med.scheduling.models.ScheduleMed;
import com.med.scheduling.repository.ScheduleRepository;
import com.med.scheduling.repository.projection.CustomScheduleMed;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

        repository.findById(id).orElseThrow(NotFoundException::new);

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
        repository.deleteById(id);
    }

    public List<MedsResponseDTO> findAllMeds() {
        List<ScheduleMed> scheduleMeds = repository.findAll();

        if(scheduleMeds.isEmpty()) throw new NotFoundException();


        return scheduleMeds.stream()
                .map(scheduleMed -> mapper.map(scheduleMed, MedsResponseDTO.class))
                .toList();
    }

    public List<MedsResponseDTO> findMedsByFilter(Pageable page, MedsFilterDTO paramsFilter) {
        List<CustomScheduleMed> medsByFilter = repository.findMedsByFilter(paramsFilter.id(),
                paramsFilter.medicationDay(),
                paramsFilter.medicationDay(),
                paramsFilter.medicationTime(),
                paramsFilter.medicationName(),
                page);

        return medsByFilter.stream()
                .map(filter -> mapper.map(filter, MedsResponseDTO.class))
                .toList();
    }
}
