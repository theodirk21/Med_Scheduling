package com.med.scheduling.service;

import com.med.scheduling.MedSchedulingProperties;
import com.med.scheduling.models.Schedule;
import com.med.scheduling.repository.ScheduleRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramBotService extends TelegramLongPollingBot {

    private final MedSchedulingProperties properties;
    private final ScheduleRepository repository;

    @Override
    public String getBotUsername() {
        return properties.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            var chatId = String.valueOf(update.getMessage().getChatId());
            var messageText = update.getMessage().getText();

            if (messageText.startsWith("/start")){
                sendMessage(chatId, "🎉 Bem-vindo ao seu bot de lembretes de medicamentos! 🏥\n" +
                        "Eu estou aqui para ajudá-lo a lembrar de tomar seus medicamentos.\n\n" +
                        "Use o comando /adicione_medicamento para agendar um lembrete. O formato é:\n" +
                        "/set_schedule <dias> <hora> <medicamento>\n" +
                        "Exemplo: /set_schedule segunda,terça 09:00 Aspirina\n\n" +
                        "Você pode usar vários dias, como \"segunda,terça\" ou \"todos\" para todos os dias.\n" +
                        "Estou aqui para ajudar! Se precisar de mais informações, use /help.");
            } else if (messageText.startsWith("/adicione_medicamento")) {
                String[] parts = messageText.split(" ", 3);
                if (parts.length < 3) {
                    sendMessage(chatId, "Uso correto: /adicione_medicamento <dias> <hora> <medicamento>\n" +
                            "Exemplo: /adicione_medicamento todos 09:00 Aspirina " +
                            "- /adicione_medicamento segunda 09:00 Dipirona ");
                    return;
                }

                String daysInput = parts[1];
                String timeInput = parts[2];

                String[] timeAndMedication = timeInput.split(" ", 2);
                if (timeAndMedication.length < 2) {
                    sendMessage(chatId, "Uso correto: /adicione_medicamento <dias> <hora> <medicamento>");
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
                    sendMessage(chatId, "Formato de hora inválido. Use o formato HH:mm.");
                    return;
                }


                days.forEach(day -> {
                    repository.save(Schedule.builder()
                            .chatId(chatId)
                            .day(day.trim())
                            .time(time)
                            .medicationName(medicationName.trim())
                            .build());
                });

                sendMessage(chatId, "Lembretes agendados para " + daysInput + " às " + time + " para o medicamento: "
                        + medicationName + ".");
            } else {
                sendMessage(chatId, "Comando não reconhecido. Use /start para ver as opções.");
            }

        }

    }

    public void sendMessage(String chatId, String message) {
        SendMessage messageSender = new SendMessage();
        messageSender.setChatId(chatId);
        messageSender.setText(message);
        try {
            execute(messageSender);
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
