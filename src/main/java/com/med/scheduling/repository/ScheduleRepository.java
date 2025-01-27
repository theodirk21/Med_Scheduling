package com.med.scheduling.repository;

import com.med.scheduling.models.ScheduleMed;
import com.med.scheduling.repository.projection.CustomScheduleMed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleMed, Long> {

    List<ScheduleMed> findByChatId(String chatId);

    List<ScheduleMed> findByMedicationDayAndMedicationTime(String localDate, LocalTime localTime);

    @Query("SELECT sm FROM ScheduleMed sm WHERE (:id IS NULL OR sm.id = :id) " +
            "AND (:chatId IS NULL OR sm.chatId = :chatId) " +
            "AND (:medicationDay IS NULL OR sm.medicationDay = :medicationDay) " +
            "AND (:medicationTime IS NULL OR sm.medicationTime = :medicationTime) " +
            "AND (:medicationName IS NULL OR sm.medicationName = :medicationName)")
    List<CustomScheduleMed> findMedsByFilter(
            @Param("id") Long id,
            @Param("chatId") String chatId,
            @Param("medicationDay") String medicationDay,
            @Param("medicationTime") LocalTime medicationTime,
            @Param("medicationName") String medicationName,
            Pageable page
    );
}
