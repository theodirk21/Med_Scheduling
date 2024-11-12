package com.med.scheduling.service;

import com.med.scheduling.exception.TelegramNotWorkingException;
import com.med.scheduling.models.MedicationState;
import com.med.scheduling.models.UserState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
    private StepsMessage stepsMessage;


    @Test
    void testOnUpdateReceived_StartCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("/iniciar");
        when(message.getChatId()).thenReturn(12345L);

        telegramBotMed.onUpdateReceived(update);

        verify(stepsMessage).iniciate("12345");
    }


    @Test
    void testOnUpdateReceived_AddMedicationCommand() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("/adicione_medicamento");
        when(message.getChatId()).thenReturn(12345L);

        doNothing().when(telegramBotMed).sendMessage(anyString(), anyString());

        telegramBotMed.onUpdateReceived(update);

        verify(chatStateService).startInteraction("12345", MedicationState.AWAITING_DAYS);
        verify(telegramBotMed).sendMessage("12345", "Em quais dias você deseja ser lembrado? (Ex.: segunda, quarta, todos)");
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

        telegramBotMed.onUpdateReceived(update);

        verify(stepsMessage).listScheduledMedications("12345");
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

        telegramBotMed.onUpdateReceived(update);

        verify(chatStateService).startInteraction("12345", MedicationState.AWAITING_DELETE);
        verify(stepsMessage).listForDelete("12345");
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

        doNothing().when(telegramBotMed).sendMessage(anyString(), anyString());

        telegramBotMed.onUpdateReceived(update);

        verify(telegramBotMed).sendMessage("12345", "Comando não reconhecido. Use /iniciar para ver as opções.");
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
        verify(stepsMessage).iniciate("12345");
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

        UserState userState = mock(UserState.class);
        when(chatStateService.getUserState("12345")).thenReturn(userState);
        when(userState.getCurrentStep()).thenReturn(MedicationState.AWAITING_DELETE);

        telegramBotMed.onUpdateReceived(update);

        verify(stepsMessage).deleteScheduleMedication("12345", "medication_id");
    }

    @Test
    void testOnUpdateReceived_ExistingUserState() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("medication_details");
        when(message.getChatId()).thenReturn(12345L);

        UserState userState = mock(UserState.class);
        when(chatStateService.getUserState("12345")).thenReturn(userState);
        when(userState.getCurrentStep()).thenReturn(MedicationState.AWAITING_DAYS);

        telegramBotMed.onUpdateReceived(update);

        verify(stepsMessage).addMedication("12345", "medication_details", userState);
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
}