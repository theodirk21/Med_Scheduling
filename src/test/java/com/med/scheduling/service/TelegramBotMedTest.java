package com.med.scheduling.service;

import com.med.scheduling.exception.ConvertTimeException;
import com.med.scheduling.exception.ReminderDayException;
import com.med.scheduling.exception.TelegramNotWorkingException;
import com.med.scheduling.models.MedicationState;
import com.med.scheduling.models.ScheduleMed;
import com.med.scheduling.models.UserState;
import com.med.scheduling.repository.ScheduleRepository;
import org.hibernate.query.sqm.sql.ConversionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramBotMedTest {

    @Mock
    private ChatStateService chatStateService;

    @InjectMocks
    @Spy
    private TelegramBotMed telegramBotMed;

    @Mock
    private ScheduleRepository scheduleRepository;


    @Test
    void testOnUpdateReceived_StartCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("/iniciar");
        when(message.getChatId()).thenReturn(12345L);
        doNothing().when(telegramBotMed).sendMessage(any(), any());

        telegramBotMed.onUpdateReceived(update);

        verify(telegramBotMed).initiate("12345");
    }


    @Test
    void testOnUpdateReceived_AddMedicationCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        UserState userState = mock(UserState.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("/adicione_medicamento");
        when(message.getChatId()).thenReturn(12345L);

        doNothing().when(telegramBotMed).sendMessage(anyString(), anyString());

        telegramBotMed.onUpdateReceived(update);

        verify(chatStateService).startInteraction("12345", MedicationState.AWAITING_DAYS);
        verify(telegramBotMed).sendMessage("12345", "Em quais dias você deseja ser lembrado? (Ex.: segunda, quarta, todos)");


        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("segunda");
        when(message.getChatId()).thenReturn(12345L);
        when(chatStateService.getUserState(any())).thenReturn(userState);
        when(userState.getCurrentStep()).thenReturn(MedicationState.AWAITING_DAYS);
        doNothing().when(telegramBotMed).sendMessage(anyString(), anyString());


        telegramBotMed.onUpdateReceived(update);

        verify(chatStateService).updateUserState(any(), eq(MedicationState.AWAITING_TIME));

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("12:00");
        when(message.getChatId()).thenReturn(12345L);
        when(chatStateService.getUserState(any())).thenReturn(userState);
        when(userState.getCurrentStep()).thenReturn(MedicationState.AWAITING_TIME);
        doNothing().when(telegramBotMed).sendMessage(anyString(), anyString());


        telegramBotMed.onUpdateReceived(update);

        verify(chatStateService).updateUserState(any(), eq(MedicationState.AWAITING_NAME));

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("medicamento de teste");
        when(message.getChatId()).thenReturn(12345L);
        when(chatStateService.getUserState(any())).thenReturn(userState);
        when(userState.getDaysOfWeek()).thenReturn(List.of("segunda-feira", "quarta-feira"));
        when(userState.getCurrentStep()).thenReturn(MedicationState.AWAITING_NAME);
        doNothing().when(telegramBotMed).sendMessage(anyString(), anyString());


        telegramBotMed.onUpdateReceived(update);

        verify(chatStateService).endInteraction(any());
        verify(scheduleRepository, times(2)).save(any());
        verify(telegramBotMed).sendMessage("12345", "Lembrete de medicamento adicionado com sucesso!");
    }

    @Test
    void testOnUpdateReceived_Error() {
        Message message = mock(Message.class);
        UserState userState = mock(UserState.class);
        when(userState.getCurrentStep()).thenReturn(MedicationState.ERROR);
        doNothing().when(telegramBotMed).sendMessage(anyString(), anyString());

        telegramBotMed.addMedication("121", message.getText(), userState);

        verify(chatStateService).endInteraction(any());

    }

    @Test
     void testAddMedication_InvalidTime() {
        String chatId = "12345";
        UserState userState = new UserState();
        userState.setCurrentStep(MedicationState.AWAITING_TIME);
        String invalidTimeInput = "notATime";
        doThrow(ConvertTimeException.class).when(telegramBotMed).convertTime(invalidTimeInput, chatId);


        assertThrows(ConvertTimeException.class, () -> {
            telegramBotMed.addMedication(chatId, invalidTimeInput, userState);
        });
    }

    @Test
     void testDeleteScheduleMedication_Success() {
        String chatId = "12345";
        String message = "1";
        Long medicationId = Long.valueOf(message);
        ScheduleMed medication = new ScheduleMed(medicationId, chatId, "segunda-feira",
                LocalTime.of(12,0), "Paracetamol");
        doNothing().when(telegramBotMed).sendMessage(anyString(), anyString());
        when(scheduleRepository.findById(medicationId)).thenReturn(Optional.of(medication));

        telegramBotMed.deleteScheduleMedication(chatId, message);

        verify(scheduleRepository, times(1)).delete(medication);
        verify(telegramBotMed).sendMessage(chatId, "Medicamento removido com sucesso!");
        verify(chatStateService, times(1)).endInteraction(chatId);
    }

    @Test
     void testDeleteScheduleMedication_MedicationNotFound() {
        String chatId = "12345";
        String message = "1";
        Long medicationId = Long.valueOf(message);

        when(scheduleRepository.findById(medicationId)).thenReturn(Optional.empty());
        doNothing().when(telegramBotMed).sendMessage(anyString(), anyString());

        telegramBotMed.deleteScheduleMedication(chatId, message);

        verify(telegramBotMed).sendMessage(chatId, "Medicamento não encontrado ou você não tem permissão para removê-lo.");
        verify(chatStateService, times(1)).endInteraction(chatId);
    }

    @Test
     void testDeleteScheduleMedication_InvalidId() {
        String chatId = "12345";
        String message = "notANumber";

        doNothing().when(telegramBotMed).sendMessage(anyString(), anyString());

        telegramBotMed.deleteScheduleMedication(chatId, message);

        verify(telegramBotMed).sendMessage(chatId, "ID inválido. Informe um número válido.");
    }

    @Test
    void testOnUpdateReceived_ScheduledMedicationsCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("/medicamentos_agendados");
        when(message.getChatId()).thenReturn(12345L);
        doNothing().when(telegramBotMed).sendMessage(any(), any());

        telegramBotMed.onUpdateReceived(update);

        verify(telegramBotMed).listScheduledMedications("12345");
    }

    @Test
    void testOnUpdateReceived_RemoveCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("/remover");
        when(message.getChatId()).thenReturn(12345L);
        doNothing().when(telegramBotMed).sendMessage(any(), any());

        telegramBotMed.onUpdateReceived(update);

        verify(chatStateService).startInteraction("12345", MedicationState.AWAITING_DELETE);
        verify(telegramBotMed).listForDelete("12345");
    }

    @Test
    void testOnUpdateReceived_UnrecognizedCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("/unknown_command");
        when(message.getChatId()).thenReturn(12345L);
        doNothing().when(telegramBotMed).sendMessage(any(), any());

        telegramBotMed.onUpdateReceived(update);

        verify(telegramBotMed).sendMessage(any(), any());
    }

    @Test
    void testOnUpdateReceived_ReiniciarCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("/reiniciar");
        when(message.getChatId()).thenReturn(12345L);

        doNothing().when(telegramBotMed).sendMessage(anyString(), anyString());

        telegramBotMed.onUpdateReceived(update);

        verify(chatStateService).endInteraction("12345");
        verify(telegramBotMed).initiate("12345");
        verify(telegramBotMed).sendMessage("12345", "Processo reiniciado. Vamos começar de novo.");
    }

    @Test
    void testOnUpdateReceived_AwaitingDeleteState() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("medication_id");
        when(message.getChatId()).thenReturn(12345L);
        doNothing().when(telegramBotMed).sendMessage(any(), any());

        UserState userState = mock(UserState.class);
        when(chatStateService.getUserState("12345")).thenReturn(userState);
        when(userState.getCurrentStep()).thenReturn(MedicationState.AWAITING_DELETE);

        telegramBotMed.onUpdateReceived(update);

        verify(telegramBotMed).deleteScheduleMedication("12345", "medication_id");
    }

    @Test
    void testOnUpdateReceived_ExistingUserState() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("quarta");
        when(message.getChatId()).thenReturn(12345L);
        doNothing().when(telegramBotMed).sendMessage(any(), any());

        UserState userState = mock(UserState.class);
        when(chatStateService.getUserState("12345")).thenReturn(userState);
        when(userState.getCurrentStep()).thenReturn(MedicationState.AWAITING_DAYS);
        telegramBotMed.onUpdateReceived(update);

        verify(telegramBotMed).addMedication("12345", "quarta", userState);
    }

    @Test
    void testSendMessage_TelegramApiException() throws TelegramApiException {
        String chatId = "12345";
        String messageText = "Hello, World!";

        doThrow(new TelegramApiException("Mock exception")).when(telegramBotMed).execute(any(SendMessage.class));

        assertThrows(TelegramNotWorkingException.class, () -> {
            telegramBotMed.sendMessage(chatId, messageText);
        });
    }

    @Test
    void testSendMessage() throws TelegramApiException {
        String chatId = "12345";
        String messageText = "Hello, World!";

        Message mockResponse = mock(Message.class);
        doReturn(mockResponse).when(telegramBotMed).execute(any(SendMessage.class));

        telegramBotMed.sendMessage(chatId, messageText);

        verify(telegramBotMed).execute(any(SendMessage.class));
    }

    @Test
     void testListScheduledMedications_NoScheduledMeds() {
        String chatId = "12345";
        when(scheduleRepository.findByChatId(chatId)).thenReturn(Collections.emptyList());
        doNothing().when(telegramBotMed).sendMessage(any(), any());

        telegramBotMed.listScheduledMedications(chatId);


        verify(telegramBotMed).sendMessage(chatId, "Não há medicamentos agendados.");
    }

    @Test
     void testListScheduledMedications_WithScheduledMeds() {
        String chatId = "12345";
        List<ScheduleMed> meds = List.of(ScheduleMed.builder()
                        .id(1L)
                .medicationName("Paracetamol")
                .medicationDay("quarta")
                .medicationTime(LocalTime.of(8, 0))
                .build(),
                ScheduleMed.builder()
                        .id(2L)
                        .medicationName("Ibuprofeno")
                        .medicationDay("sexta")
                        .medicationTime(LocalTime.of(9, 0))
                        .build());
        when(scheduleRepository.findByChatId(chatId)).thenReturn(meds);
        doNothing().when(telegramBotMed).sendMessage(any(), any());


        telegramBotMed.listScheduledMedications(chatId);

        String expectedMessage = """
Seus medicamentos agendados:

ID: 1
Medicamento: Paracetamol
Dia: quarta
Horário: 08:00
-----------------------------------

ID: 2
Medicamento: Ibuprofeno
Dia: sexta
Horário: 09:00
-----------------------------------

            """;

        verify(telegramBotMed).sendMessage(chatId, expectedMessage);
        verify(telegramBotMed).listScheduledMedications(chatId);
    }

    @Test
     void testReminderDays_ReturnsAllDays_WhenInputIsTodos() {
        String daysInput = "todos";
        String chatId = "12345";

        List<String> result = telegramBotMed.reminderDays(daysInput, chatId);


        List<String> expected = List.of("segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira", "sábado", "domingo");
        assertEquals(expected, result);
    }


    @Test
     void testReminderDays_ReturnsValidDays_WhenInputIsValid() {
        String daysInput = "segunda, terça";
        String chatId = "12345";

        List<String> result = telegramBotMed.reminderDays(daysInput, chatId);

        List<String> expected = List.of("segunda-feira", "terça-feira");
        assertEquals(expected, result);
    }

    @Test
     void testReminderDays_ThrowsException_WhenInputContainsInvalidDays() {
        String daysInput = "segunda, diaX";
        String chatId = "12345";
        doNothing().when(telegramBotMed).sendMessage(any(), any());
        Exception exception = assertThrows(ReminderDayException.class, () -> {
            telegramBotMed.reminderDays(daysInput, chatId);
        });

        verify(telegramBotMed).sendMessage(chatId, """
            Um ou mais dias estão incorretos. Por favor, informe os dias novamente.\

            Lembre-se que precisa se algo como segunda, terça (se forem mais de um dia precisam ser separados por virgula)\s
            caso deseje adicionar para todos os dias da semana escreva 'todos'

            Caso queira reiniciar use o /reiniciar""");
        assertEquals("Dias informados errados", exception.getMessage());
    }

    @Test
     void testReminderDays_ReturnsValidDays_WithDifferentFormatting() {
        String daysInput = "Segunda, terça-feira, quinta, sexta, sabado, domingo ";
        String chatId = "12345";

        List<String> result = telegramBotMed.reminderDays(daysInput, chatId);

        List<String> expected = List.of("segunda-feira", "terça-feira", "quinta-feira", "sexta-feira",
                "sábado", "domingo");
        assertEquals(expected, result);
    }
}