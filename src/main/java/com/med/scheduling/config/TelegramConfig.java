package com.med.scheduling.config;

import com.med.scheduling.MedSchedulingProperties;
import com.med.scheduling.repository.ScheduleRepository;
import com.med.scheduling.service.ChatStateService;
import com.med.scheduling.service.TelegramBotMed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class TelegramConfig {

    private final MedSchedulingProperties properties;

    private final ChatStateService chatStateService;

    private final ScheduleRepository repository;

    @Bean
    public TelegramBotMed telegramBot() {
        TelegramBotMed telegramBot = new TelegramBotMed(
                properties.getBotToken(),
                properties.getBotName(),
                chatStateService,
                repository
        );
        try {
            var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            log.error("Exception during registration telegram api: {}", e.getMessage());
        }
        return telegramBot;
    }
}
