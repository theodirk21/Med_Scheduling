package com.med.scheduling.service;

import com.med.scheduling.models.ScheduleMed;
import com.med.scheduling.repository.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;

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

        if (update.hasMessage() && update.getMessage().hasText()) {

            var messageText = update.getMessage().getText();
            var chatId = update.getMessage().getChatId();

            log.info("Chat ID: {}", chatId);
            log.info("Received message: {}", messageText);

            String command = messageText.split(" ")[0];

            switch (command) {
                case "/iniciar" -> iniciate(chatId);
                case "/adicione_medicamento" -> addMedication(messageText, chatId);
                case "/medicamentos_agendados" -> listScheduledMedications(chatId);
                default -> sendMessage(String.valueOf(chatId), "Comando não reconhecido. Use /iniciar para ver as opções.");
            }
        }
    }

    public void iniciate(Long chatId) {

            sendMessage(String.valueOf(chatId),
                    "🎉 Bem-vindo ao seu bot de lembretes de medicamentos! 🏥\n" +
                            "Eu estou aqui para ajudá-lo a lembrar de tomar seus medicamentos.\n\n" +
                            "Use o comando /adicione_medicamento para agendar um lembrete. O formato é:\n" +
                            "/adicione_medicamento <dias> <hora> <medicamento>\n" +
                            "Exemplo: /adicione_medicamento segunda,terça 09:00 Aspirina\n\n" +
                            " Obs: Você pode usar vários dias, como \"segunda,terça\" ou \"todos\" para todos os dias.\n" +
                            "Você pode também conferir seus agendamentos ja armazenados utilizando o /medicamentos_agendados !!");
        }

    public void addMedication(String messageText, Long chatId){
        String[] parts = messageText.split(" ", 3);
        if (parts.length < 3) {
            sendMessage(String.valueOf(chatId), "Uso correto: /adicione_medicamento <dias> <hora> <medicamento>\n" +
                    "Exemplo: /adicione_medicamento todos 09:00 Aspirina " +
                    "- /adicione_medicamento segunda-feira 09:00 Dipirona ");
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

        List<String> days = reminderDays(daysInput, chatId.toString());

        LocalTime time;
        try {
            time = LocalTime.parse(timeString);
        } catch (DateTimeParseException e) {
            sendMessage(String.valueOf(chatId), "Formato de hora inválido. Use o formato HH:mm.");
            return;
        }


        days.forEach(day -> {
            ScheduleMed scheduleMed = repository.save(ScheduleMed.builder()
                    .chatId(String.valueOf(chatId))
                    .medicationDay(day.trim())
                    .medicationTime(time)
                    .medicationName(medicationName.trim())
                    .build());
        });

        sendMessage(String.valueOf(chatId), "Lembretes agendados para " + daysInput + " às " + time + " para o medicamento: "
                + medicationName + ".");
    }

    public void listScheduledMedications(Long chatId) {
        List<ScheduleMed> scheduledMeds = repository.findByChatId(String.valueOf(chatId));

        if (scheduledMeds.isEmpty()) {
            sendMessage(String.valueOf(chatId), "Não há medicamentos agendados.");
            return;
        }

        StringBuilder messageBuilder = new StringBuilder("Seus medicamentos agendados:\n\n");

        for (ScheduleMed med : scheduledMeds) {
            messageBuilder.append("Medicamento: ").append(med.getMedicationName())
                    .append("\nDia: ").append(med.getMedicationDay())
                    .append("\nHorário").append(med.getMedicationTime())
                    .append("\n\n");
        }

        sendMessage(String.valueOf(chatId), messageBuilder.toString());
    }

    public List<String> reminderDays(String daysInput, String chatId) {
        List<String> allDays = List.of("segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira",
                "sábado", "domingo");

        if (daysInput.equalsIgnoreCase("todos")) {
            return allDays;
        }

        return Arrays.stream(daysInput.split(","))
                .map(String::trim)
                .map(day -> {
                    switch (day.toLowerCase()) {
                        case "segunda":
                            return "segunda-feira";
                        case "terça":
                            return "terça-feira";
                        case "quarta":
                            return "quarta-feira";
                        case "quinta":
                            return "quinta-feira";
                        case "sexta":
                            return "sexta-feira";
                        case "sábado":
                            return "sábado";
                        case "domingo":
                            return "domingo";
                        default:
                             sendMessage(chatId, "Dia da semana incorreto");
                             return null;
                    }
                })
                .filter(Objects::nonNull) // Remove entradas nulas
                .toList();
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
