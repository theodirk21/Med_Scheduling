package com.med.scheduling.service;

import com.med.scheduling.models.ScheduleMed;
import com.med.scheduling.repository.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class TelegramBotMed extends TelegramLongPollingBot {

    private final String botName;

    @Autowired
    private ScheduleRepository repository;

    public TelegramBotMed(String botName, String botToken) {
        super(botToken);
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Update iniciado");

        if (update.hasMessage() && update.getMessage().hasText()) {
            var messageText = update.getMessage().getText();
            var chatId = update.getMessage().getChatId();

            System.out.println("Chat ID: " + chatId);
            System.out.println("Received message: " + messageText);

            if (messageText.startsWith("/iniciar")) {
                sendMessage(String.valueOf(chatId),
                        "🎉 Bem-vindo ao seu bot de lembretes de medicamentos! 🏥\n" +
                                "Eu estou aqui para ajudá-lo a lembrar de tomar seus medicamentos.\n\n" +
                                "Use o comando /adicione_medicamento para agendar um lembrete. O formato é:\n" +
                                "/adicione_medicamento <dias> <hora> <medicamento>\n" +
                                "Exemplo: /adicione_medicamento segunda,terça 09:00 Aspirina\n\n" +
                                " Obs: Você pode usar vários dias, como \"segunda,terça\" ou \"todos\" para todos os dias.\n");
            }
            else if (messageText.startsWith("/adicione_medicamento")) {
                String[] parts = messageText.split(" ", 3);
                if (parts.length < 3) {
                    sendMessage(String.valueOf(chatId), "Uso correto: /adicione_medicamento <dias> <hora> <medicamento>\n" +
                            "Exemplo: /adicione_medicamento todos 09:00 Aspirina " +
                            "- /adicione_medicamento segunda 09:00 Dipirona ");
                    return;
                }

                String daysInput = parts[1];
                String timeInput = parts[2];

                String[] timeAndMedication = timeInput.split(" ", 2);
                if (timeAndMedication.length < 2) {
                    sendMessage(String.valueOf(chatId), "Uso correto: /adicione_medicamento <dias> <hora> <medicamento>");
                    return;
                }

                String timeString = timeAndMedication[0];
                String medicationName = timeAndMedication[1];

                List<String> days = daysInput.equalsIgnoreCase("todos")
                        ? List.of("segunda", "terça", "quarta", "quinta", "sexta", "sábado", "domingo")
                        : Arrays.asList(daysInput.split(","));

                LocalTime time;
                try {
                    time = LocalTime.parse(timeString);
                } catch (DateTimeParseException e) {
                    sendMessage(String.valueOf(chatId), "Formato de hora inválido. Use o formato HH:mm.");
                    return;
                }


                days.forEach(day -> repository.save(ScheduleMed.builder()
                        .chatId(String.valueOf(chatId))
                        .medicationDay(day.trim())
                        .medicationTime(time)
                        .medicationName(medicationName.trim())
                        .build()));

                sendMessage(String.valueOf(chatId), "Lembretes agendados para " + daysInput + " às " + time + " para o medicamento: "
                        + medicationName + ".");
            }
            else {
                sendMessage(String.valueOf(chatId), "Comando não reconhecido. Use /iniciar para ver as opções.");
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
            e.printStackTrace();
        }
    }


    @Override
    public String getBotUsername() {
        return this.botName;
    }
}
