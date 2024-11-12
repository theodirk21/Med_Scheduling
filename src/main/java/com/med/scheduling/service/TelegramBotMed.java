package com.med.scheduling.service;

import com.med.scheduling.exception.TelegramNotWorkingException;
import com.med.scheduling.models.MedicationState;
import com.med.scheduling.models.UserState;
import com.med.scheduling.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component

public class TelegramBotMed extends TelegramLongPollingBot {

    private final String botName;

    private final ChatStateService chatStateService;

    private final StepsMessage stepsMessage;

    public TelegramBotMed(String botName, String botToken, ChatStateService chatStateService
    , StepsMessage stepsMessage) {
        super(botToken);
        this.botName = botName;
        this.chatStateService = chatStateService;
        this.stepsMessage = stepsMessage;

    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {

            var messageText = update.getMessage().getText();
            var chatId = update.getMessage().getChatId().toString();

            log.info("Chat ID: {}", chatId);
            log.info("Received message: {}", messageText);

            UserState userState = chatStateService.getUserState(chatId);

            if (messageText.equals("/reiniciar")) {
                chatStateService.endInteraction(chatId);
                sendMessage(chatId, "Processo reiniciado. Vamos começar de novo.");
                stepsMessage.iniciate(chatId);
                return;
            }
            else if (userState != null) {
                if(userState.getCurrentStep() == MedicationState.AWAITING_DELETE){
                    stepsMessage.deleteScheduleMedication(chatId, messageText);
                    return;
                }
                stepsMessage.addMedication(chatId, messageText, userState);
                return;
            }

            switch (messageText) {
                case "/iniciar":
                    stepsMessage.iniciate(chatId);
                    break;
                case "/adicione_medicamento":
                    chatStateService.startInteraction(chatId, MedicationState.AWAITING_DAYS);
                    sendMessage(chatId, "Em quais dias você deseja ser lembrado? (Ex.: segunda, quarta, todos)");
                    break;
                case "/medicamentos_agendados":
                    stepsMessage.listScheduledMedications(chatId);
                    break;
                case "/remover":
                    chatStateService.startInteraction(chatId, MedicationState.AWAITING_DELETE);
                    stepsMessage.listForDelete(chatId);
                    break;
                default:
                    sendMessage(String.valueOf(chatId), "Comando não reconhecido. Use /iniciar para ver as opções.");
                    break;
            }
        }
    }

    public void sendMessage(String chatId, String message) {
        SendMessage messageSender = new SendMessage();
        messageSender.setChatId(chatId);
        messageSender.setText(message);
        try {
            execute(messageSender);
        } catch (TelegramApiException e) {
           throw new TelegramNotWorkingException("Telegram teve problemas no envio da mensagem");
        }
    }


    @Override
    public String getBotUsername() {
        return this.botName;
    }
}
