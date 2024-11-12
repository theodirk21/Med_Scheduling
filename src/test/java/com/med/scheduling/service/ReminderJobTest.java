package com.med.scheduling.service;

import com.med.scheduling.models.ScheduleMed;
import com.med.scheduling.repository.ScheduleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.*;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderJobTest {

    @InjectMocks
    private ReminderJob reminderJob;

    @Mock
    private TelegramBotMed telegramService;

    @Mock
    private ScheduleRepository repository;

    @Test
    void verificarLembretesComLembreteExistente() {

        var chatId = "345";
        var medName = "MedMock";
        var diaDaSemana = DayOfWeek.from(LocalDate.now()).getDisplayName(TextStyle.FULL,
                new Locale("pt", "BR"));

        var lembrete = ScheduleMed.builder()
                .chatId(chatId)
                .medicationName(medName)
                .build();

        when(repository.findByMedicationDayAndMedicationTime(diaDaSemana, LocalTime.now().withSecond(0).withNano(0)))
                .thenReturn(List.of(lembrete));

        reminderJob.verificarLembretes();

        verify(telegramService, times(1)).sendMessage(eq(chatId),
                contains("Lembrete: É hora de tomar " + medName));
    }

    @Test
    void verificarLembretesSemLembrete() {

        var chatId = "345";
        var medName = "MedMock";
        var diaDaSemana = DayOfWeek.from(LocalDate.now()).getDisplayName(TextStyle.FULL,
                new Locale("pt", "BR"));

        when(repository.findByMedicationDayAndMedicationTime(diaDaSemana, LocalTime.now().withSecond(0).withNano(0)))
                .thenReturn(List.of());

        reminderJob.verificarLembretes();

        verify(telegramService, never()).sendMessage(any(),
                any());
    }
}