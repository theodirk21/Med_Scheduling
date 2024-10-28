package com.med.scheduling.service;

import com.med.scheduling.models.ScheduleMed;
import com.med.scheduling.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ReminderJob {


    private final TelegramBotMed telegramService;

    private final ScheduleRepository repository;

    @Scheduled(fixedRate = 60000)
    public void verificarLembretes() {
        var diaDaSemana = DayOfWeek.from(LocalDate.now()).getDisplayName(TextStyle.FULL,
                new Locale("pt", "BR"));
        LocalTime horaAtual = LocalTime.now().withSecond(0).withNano(0);


        List<ScheduleMed> lembretes = repository
                .findByMedicationDayAndMedicationTime(diaDaSemana.toLowerCase(),horaAtual);

        lembretes.forEach(lembrete -> {
            telegramService.sendMessage(lembrete.getChatId(),
                    "Lembrete: É hora de tomar " + lembrete.getMedicationName());
        });
    }
}

