//package com.med.scheduling.repository;
//
//import com.med.scheduling.models.ScheduleMed;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.time.LocalTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//@ActiveProfiles("test")
//class ScheduleRepositoryTest {
//
//    @Autowired
//    private TestEntityManager testEntityManager;
//
//    @Autowired
//    private ScheduleRepository scheduleRepository;
//
//    private LocalTime dateMed;
//
//    @BeforeEach
//    void setUp(){
//
//        dateMed = LocalTime.now();
//
//
//        testEntityManager.persist(ScheduleMed.builder()
//                .chatId("chatid1")
//                .medicationDay("Seg")
//                .medicationTime(dateMed)
//                .medicationName("medication")
//                .build());
//    }
//
//    @Test
//    void findByChatId() {
//
//        List<ScheduleMed> meds = scheduleRepository.findByChatId("chatid1");
//
//        assertNotNull(meds);
//        assertEquals("Seg", meds.get(0).getMedicationDay());
//        assertEquals("medication", meds.get(0).getMedicationName());
//        assertEquals(dateMed, meds.get(0).getMedicationTime());
//    }
//
//    @Test
//    void findByMedicationDayAndMedicationTime() {
//
//        var meds = scheduleRepository.findByMedicationDayAndMedicationTime("Seg", dateMed);
//
//        assertNotNull(meds);
//        assertEquals("Seg", meds.get(0).getMedicationDay());
//        assertEquals("medication", meds.get(0).getMedicationName());
//        assertEquals(dateMed, meds.get(0).getMedicationTime());
//
//    }
//}