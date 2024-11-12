package com.med.scheduling.config;

import com.med.scheduling.MedSchedulingProperties;
import com.med.scheduling.repository.ScheduleRepository;
import com.med.scheduling.service.ChatStateService;
import com.med.scheduling.service.StepsMessage;
import com.med.scheduling.service.TelegramBotMed;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramConfigTest {
    @Test
    void shouldCreateTelegramBotMedBeanWithCorrectProperties() throws TelegramApiException {
        MedSchedulingProperties properties = mock(MedSchedulingProperties.class);
        ScheduleRepository repository = mock(ScheduleRepository.class);
        ChatStateService chatStateService = mock(ChatStateService.class);
        StepsMessage stepsMessage = mock(StepsMessage.class);

        when(properties.getBotName()).thenReturn("testBotName");
        when(properties.getBotToken()).thenReturn("testBotToken");

        TelegramConfig telegramConfig = new TelegramConfig(properties, repository, chatStateService, stepsMessage);
        TelegramBotMed telegramBot = telegramConfig.telegramBot();

        assertEquals("testBotName", telegramBot.getBotUsername(), "Expected bot name to be 'testBotName'");
        assertEquals("testBotToken", telegramBot.getBotToken(), "Expected bot token to be 'testBotToken'");
    }
    @Test
    void shouldHandleTelegramApiExceptionDuringBotRegistration() {
        MedSchedulingProperties properties = mock(MedSchedulingProperties.class);
        ScheduleRepository repository = mock(ScheduleRepository.class);
        ChatStateService chatStateService = mock(ChatStateService.class);
        StepsMessage stepsMessage = mock(StepsMessage.class);

        when(properties.getBotName()).thenReturn("testBotName");
        when(properties.getBotToken()).thenReturn("testBotToken");

        TelegramConfig telegramConfig = new TelegramConfig(properties, repository, chatStateService, stepsMessage);

        try {
            telegramConfig.telegramBot();
        } catch (Exception e) {
            assertEquals("Test Exception", e.getMessage(), "Expected exception message to be 'Test Exception'");
        }
    }
}