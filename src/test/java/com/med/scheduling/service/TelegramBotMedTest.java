package com.med.scheduling.service;

import com.med.scheduling.repository.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramBotMedTest {

    @Mock
    private ScheduleRepository repository;

    @Mock
    private ChatStateService chatStateService;

    @InjectMocks
    private TelegramBotMed telegramBotMed;

    @Mock
    private SendMessage sendMessage;

    @Mock
    private StepsMessage testeRemover;

    @Mock
    private Update update;

    @Mock
    private Message message;

    @Test
    void onUpdateReceived_startCommand() {

        String chatId = "1211";

        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message); // Retorna o mock de Message
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn("/iniciar");
        when(message.getChatId()).thenReturn(Long.valueOf(chatId));
        when(chatStateService.getUserState(any())).thenReturn(null);

        telegramBotMed.onUpdateReceived(update);

        verify(testeRemover).iniciate(any());
    }

    @Test
    void iniciate() {
    }

    @Test
    void addMedication() {
    }

    @Test
    void reminderDays() {
    }

    @Test
    void listScheduledMedications() {
    }

    @Test
    void sendMessage() {
    }

    @Test
    void getBotUsername() {
    }
}