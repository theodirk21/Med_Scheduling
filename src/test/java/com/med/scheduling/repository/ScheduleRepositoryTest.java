package com.med.scheduling.repository;

import com.med.scheduling.models.ScheduleMed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ScheduleRepositoryTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    private LocalTime dateMed;

    private ScheduleMed med;

    @BeforeEach
    void setUp(){

        dateMed = LocalTime.now();


       med = ScheduleMed.builder()
                .chatId("chatid1")
                .medicationDay("Seg")
                .medicationTime(dateMed)
                .medicationName("medication")
                .build();

       scheduleRepository.save(med);
    }

    @Test
    void findByChatId() {

        List<ScheduleMed> meds = scheduleRepository.findByChatId("chatid1");

        assertNotNull(meds);
        assertEquals("Seg", meds.get(0).getMedicationDay());
        assertEquals("medication", meds.get(0).getMedicationName());
        assertEquals(dateMed, meds.get(0).getMedicationTime());
    }

    @Test
    void findByMedicationDayAndMedicationTime() {

        var meds = scheduleRepository.findByMedicationDayAndMedicationTime("Seg", dateMed);

        assertNotNull(meds);
        assertEquals("Seg", meds.get(0).getMedicationDay());
        assertEquals("medication", meds.get(0).getMedicationName());
        assertEquals(dateMed, meds.get(0).getMedicationTime());

    }

    @Test
    void dontFindAny() {

        var meds = scheduleRepository.findMedsByFilter(null, null, "ter",
                null, null, Pageable.ofSize(1));

        assertNotNull(meds);
       assertEquals(0, meds.size());
    }

    @Test
    void findMedsByFilter() {

        var meds = scheduleRepository.findMedsByFilter(null, null, "Seg",
                dateMed, "medication", Pageable.ofSize(1));

        assertNotNull(meds);
        assertEquals(1, meds.size());
        assertEquals("Seg", meds.get(0).getMedicationDay());
        assertEquals("medication", meds.get(0).getMedicationName());
        assertEquals(dateMed, meds.get(0).getMedicationTime());
        assertEquals(med.getChatId(), meds.get(0).getChatId());
        assertEquals(med.getId(), meds.get(0).getId());
    }

    @Test
    void findAll(){

        ScheduleMed med2 = ScheduleMed.builder()
                .chatId("chatid2")
                .medicationDay("Ter")
                .medicationTime(dateMed)
                .medicationName("medication2")
                .build();

        scheduleRepository.save(med2);

        List<ScheduleMed> scheduleMeds = scheduleRepository.findAll();

        assertNotNull(scheduleMeds);
        assertEquals(2, scheduleMeds.size());

        //1
        assertEquals("Seg", scheduleMeds.get(0).getMedicationDay());
        assertEquals("medication", scheduleMeds.get(0).getMedicationName());
        assertEquals(dateMed, scheduleMeds.get(0).getMedicationTime());
        assertEquals(med.getChatId(), scheduleMeds.get(0).getChatId());
        assertEquals(med.getId(), scheduleMeds.get(0).getId());

        //2
        assertEquals("Ter", scheduleMeds.get(1).getMedicationDay());
        assertEquals("medication2", scheduleMeds.get(1).getMedicationName());
        assertEquals(dateMed, scheduleMeds.get(1).getMedicationTime());
        assertEquals(med2.getChatId(), scheduleMeds.get(1).getChatId());
        assertEquals(med2.getId(), scheduleMeds.get(1).getId());

    }
}