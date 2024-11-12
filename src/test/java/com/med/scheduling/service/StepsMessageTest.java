package com.med.scheduling.service;

import com.med.scheduling.exception.ConvertTimeException;
import com.med.scheduling.exception.ReminderDayException;
import com.med.scheduling.models.MedicationState;
import com.med.scheduling.models.ScheduleMed;
import com.med.scheduling.models.UserState;
import com.med.scheduling.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StepsMessageTest {

    @Mock
    private TelegramBotMed telegramBotMed;

    @Mock
    private ScheduleRepository repository;

    @Mock
    private ChatStateService chatStateService;

    @InjectMocks
    private StepsMessage stepsMessage;

    private String chatId;
    private UserState userState;

    @BeforeEach
    void setUp() {
        chatId = "12345";
        userState = new UserState();
        userState.setCurrentStep(MedicationState.AWAITING_DAYS);
    }

    @Test
    void testListForDelete() {
        stepsMessage.listForDelete(chatId);
        verify(telegramBotMed).sendMessage(chatId, "Para remover um medicamento, digite o ID correspondente.");
    }

    @Test
    void testDeleteScheduleMedication_Success() {
        Long medicationId = 1L;
        ScheduleMed scheduledMed = new ScheduleMed();
        scheduledMed.setId(medicationId);
        scheduledMed.setChatId(chatId);

        when(repository.findById(medicationId)).thenReturn(Optional.of(scheduledMed));

        stepsMessage.deleteScheduleMedication(chatId, String.valueOf(medicationId));

        verify(repository).delete(scheduledMed);
        verify(telegramBotMed).sendMessage(chatId, "Medicamento removido com sucesso!");
        verify(chatStateService).endInteraction(chatId);
    }

    @Test
    void testDeleteScheduleMedication_InvalidId() {
        stepsMessage.deleteScheduleMedication(chatId, "invalid");

        verify(telegramBotMed).sendMessage(chatId, "ID inválido. Informe um número válido.");
    }

    @Test
    void testAddMedication_AwaitingDays() {
        stepsMessage.addMedication(chatId, "segunda, terça", userState);
        verify(chatStateService).updateUserState(chatId, MedicationState.AWAITING_TIME);
        verify(telegramBotMed).sendMessage(chatId, "Qual horário? (Ex.: 12:00)");
    }

    @Test
    void testAddMedication_AwaitingTime() {
        userState.setCurrentStep(MedicationState.AWAITING_TIME);
        stepsMessage.addMedication(chatId, "12:00", userState);
        verify(chatStateService).updateUserState(chatId, MedicationState.AWAITING_NAME);
        verify(telegramBotMed).sendMessage(chatId,  "Qual é o nome do medicamento?");
    }

    @Test
    void testAddMedication_AwaitingName() {
        userState.setCurrentStep(MedicationState.AWAITING_NAME);
        stepsMessage.addMedication(chatId, "medicamento teste", userState);
        verify(chatStateService).endInteraction(chatId);
        verify(telegramBotMed).sendMessage(chatId,  "Lembrete de medicamento adicionado com sucesso!");
    }

    @Test
    void testConvertTime_ValidTime() {
        LocalTime time = stepsMessage.convertTime("12:00", chatId);
        assertEquals(LocalTime.of(12, 0), time);
    }

    @Test
    void testConvertTime_InvalidTime() {
        Exception exception = assertThrows(ConvertTimeException.class, () -> {
            stepsMessage.convertTime("invalid", chatId);
        });

        verify(telegramBotMed).sendMessage(chatId, "Hora inválida. Use o formato HH:mm.");
        assertEquals("Formato de hora inválido.", exception.getMessage());
    }

    @Test
    void testReminderDays_ValidInput() {
        var days = stepsMessage.reminderDays("segunda, terça", chatId);
        assertEquals(2, days.size());
    }

    @Test
    void testReminderDays_InvalidInput() {
        Exception exception = assertThrows(ReminderDayException.class, () -> {
            stepsMessage.reminderDays("invalid", chatId);
        });

        verify(telegramBotMed).sendMessage(anyString(), anyString());
        assertEquals("Dias informados errados", exception.getMessage());
    }

}