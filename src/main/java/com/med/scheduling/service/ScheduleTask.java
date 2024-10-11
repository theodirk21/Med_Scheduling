//package com.med.scheduling.service;
//
//import com.med.scheduling.MedSchedulingProperties;
//
//import com.med.scheduling.repository.ScheduleRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.Arrays;
//
//
//@Service
//@RequiredArgsConstructor
//public class ScheduleTask {
//
//    @Autowired
//    private ScheduleRepository repository;
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    @Autowired
//    private TelegramBotService telegramBotService;
//
//    private final MedSchedulingProperties properties;
//
//    @Scheduled(fixedRate = 60000)
//    public void checkScheduledTasks(){
//        var dayOfWeek = LocalDate.now().getDayOfWeek().name().toLowerCase();
//        var currentTime =  LocalTime.now().withSecond(0).withNano(0);
//
//        repository.findAll().forEach(schedule -> {
//            if (Arrays.asList(schedule.getDay().split(",")).contains(dayOfWeek)
//            && currentTime.equals(schedule.getTime().withSecond(0).withNano(0))) {
//                var message = schedule.getChatId() + ":Hora de tomar seu medicamento: "+ schedule.getMedicationName() + "!";
//                rabbitTemplate.convertAndSend(properties.getRabbitQueueName(), message);
//            }
//        });
//
//    }
//
//}
