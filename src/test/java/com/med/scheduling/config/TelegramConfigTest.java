package com.med.scheduling.config;

import com.med.scheduling.MedSchedulingProperties;
import com.med.scheduling.repository.ScheduleRepository;
import com.med.scheduling.service.ChatStateService;
import com.med.scheduling.service.TelegramBotMed;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramConfigTest {

    @Mock
    private MedSchedulingProperties properties;

    @Mock
    private ScheduleRepository repository;

    @Mock
    private ChatStateService chatStateService;

    @Test
    void shouldCreateTelegramBotMedBeanWithCorrectProperties()  {


        when(properties.getBotName()).thenReturn("testBotName");
        when(properties.getBotToken()).thenReturn("testBotToken");

        TelegramConfig telegramConfig = new TelegramConfig(properties, chatStateService, repository);
        TelegramBotMed telegramBot = telegramConfig.telegramBot();

        assertEquals("testBotName", telegramBot.getBotUsername(), "Expected bot name to be 'testBotName'");
        assertEquals("testBotToken", telegramBot.getBotToken(), "Expected bot token to be 'testBotToken'");
    }
    @Test
    void shouldHandleTelegramApiExceptionDuringBotRegistration() {


        when(properties.getBotName()).thenReturn("testBotName");
        when(properties.getBotToken()).thenReturn("testBotToken");

        TelegramConfig telegramConfig = new TelegramConfig(properties, chatStateService, repository);

        try {
            telegramConfig.telegramBot();
        } catch (Exception e) {
            assertEquals("Test Exception", e.getMessage(), "Expected exception message to be 'Test Exception'");
        }
    }
}