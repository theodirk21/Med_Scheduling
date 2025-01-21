package com.med.scheduling.repository;

import com.med.scheduling.models.ScheduleMed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleMed, Long> {

    List<ScheduleMed> findByChatId(String chatId);

    List<ScheduleMed> findByMedicationDayAndMedicationTime(String localDate, LocalTime localTime);
}
