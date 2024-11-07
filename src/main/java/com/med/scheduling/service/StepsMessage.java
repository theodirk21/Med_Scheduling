package com.med.scheduling.service;

import com.med.scheduling.exception.ConvertTimeException;
import com.med.scheduling.exception.ReminderDayException;
import com.med.scheduling.models.MedicationState;
import com.med.scheduling.models.ScheduleMed;
import com.med.scheduling.models.UserState;
import com.med.scheduling.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StepsMessage {

private final TelegramBotMed telegramBotMed;

private final ScheduleRepository repository;

private final ChatStateService chatStateService;

    public void listForDelete(String chatId) {
        listScheduledMedications(chatId);
        telegramBotMed.sendMessage(chatId, "Para remover um medicamento, digite o ID correspondente.");
    }

    public void deleteScheduleMedication(String chatId, String message) {
        try {
            Long medicationId = Long.valueOf(message);
            var optionalMed = repository.findById(medicationId);
            if (optionalMed.isPresent() && optionalMed.get().getChatId().equals(chatId)) {
                repository.delete(optionalMed.get());
                telegramBotMed.sendMessage(chatId, "Medicamento removido com sucesso!");
                chatStateService.endInteraction(chatId);
            } else {
                telegramBotMed.sendMessage(chatId, "Medicamento não encontrado ou você não tem permissão para removê-lo.");
                iniciate(chatId);
                chatStateService.endInteraction(chatId);
            }
        } catch (NumberFormatException e) {
            telegramBotMed.sendMessage(chatId, "ID inválido. Informe um número válido.");
        }
    }

    public void iniciate(String chatId) {

        telegramBotMed.sendMessage(chatId,
                """
                        🎉 Bem-vindo ao seu bot de lembretes de medicamentos! 🏥
                        Eu estou aqui para ajudá-lo a lembrar de tomar seus medicamentos.

                        Use o comando /adicione_medicamento para agendar um lembrete.
                        Obs: Você pode usar vários dias, como "segunda,terça" ou "todos" para todos os dias.
                        Você pode também conferir seus agendamentos ja armazenados utilizando o /medicamentos_agendados ou\s
                        se preferir deletar algum agendamento só usar o /remover""");
    }

    public void addMedication(String chatId, String messageText, UserState userState) {
        switch (userState.getCurrentStep()){
            case AWAITING_DAYS:
                var days = reminderDays(messageText, chatId);
                userState.setDaysOfWeek(days);
                chatStateService.updateUserState(chatId, MedicationState.AWAITING_TIME);
                telegramBotMed.sendMessage(chatId,"Qual horário? (Ex.: 12:00)");
                break;
            case AWAITING_TIME:
                var time = convertTime(messageText, chatId);
                userState.setTime(time);
                chatStateService.updateUserState(chatId, MedicationState.AWAITING_NAME);
                telegramBotMed.sendMessage(chatId, "Qual é o nome do medicamento?");
                break;
            case AWAITING_NAME:
                userState.setMedicationName(messageText);
                chatStateService.endInteraction(chatId);
                userState.getDaysOfWeek().forEach(day -> {
                    repository.save(ScheduleMed.builder()
                            .chatId(chatId)
                            .medicationDay(day.trim())
                            .medicationTime(userState.getTime())
                            .medicationName(userState.getMedicationName())
                            .build());});
                telegramBotMed.sendMessage(chatId, "Lembrete de medicamento adicionado com sucesso!");
                break;
            default:
                chatStateService.endInteraction(chatId);
                telegramBotMed.sendMessage(chatId, "Ocorreu um erro. Por favor, tente novamente.");
                break;
        }
    }

    private LocalTime convertTime(String messageText, String chatId) {
        LocalTime time = null;
        try {
            time = LocalTime.parse(messageText);
        } catch (DateTimeParseException e) {
            telegramBotMed.sendMessage(chatId, "Hora inválida. Use o formato HH:mm.");
            throw new ConvertTimeException("Formato de hora inválido.");
        }
        return time;
    }

    public List<String> reminderDays(String daysInput, String chatId) {
        List<String> allDays = List.of("segunda-feira", "terça-feira", "quarta-feira", "quinta-feira", "sexta-feira",
                "sábado", "domingo");

        if (daysInput.equalsIgnoreCase("todos")) {
            return allDays;
        }

        List<String> validDays = new ArrayList<>();

        for (String day : daysInput.split(",")) {
            String trimmedDay = day.trim().toLowerCase();
            switch (trimmedDay) {
                case "segunda":
                case "segunda-feira":
                    validDays.add("segunda-feira");
                    break;
                case "terça":
                case "terça-feira":
                    validDays.add("terça-feira");
                    break;
                case "quarta":
                case "quarta-feira":
                    validDays.add("quarta-feira");
                    break;
                case "quinta":
                case "quinta-feira":
                    validDays.add("quinta-feira");
                    break;
                case "sexta":
                case "sexta-feira":
                    validDays.add("sexta-feira");
                    break;
                case "sábado":
                    validDays.add("sábado");
                    break;
                case "domingo":
                    validDays.add("domingo");
                    break;
                default:
                    telegramBotMed.sendMessage(chatId, """
                            Um ou mais dias estão incorretos. Por favor, informe os dias novamente.\

                            Lembre-se que precisa se algo como segunda, terça (se forem mais de um dia precisam ser separados por virgula)\s
                            caso deseje adicionar para todos os dias da semana escreva 'todos'

                            Caso queira reiniciar use o /reiniciar""");
                    throw new ReminderDayException("Dias informados errados");
            }
        }

        return validDays;
    }

    public void listScheduledMedications(String chatId) {
        List<ScheduleMed> scheduledMeds = repository.findByChatId(chatId);

        if (scheduledMeds.isEmpty()) {
            telegramBotMed.sendMessage(chatId, "Não há medicamentos agendados.");
            return;
        }

        StringBuilder messageBuilder = new StringBuilder("Seus medicamentos agendados:\n\n");

        for (ScheduleMed med : scheduledMeds) {
            messageBuilder.append("ID: ").append(med.getId())
                    .append("\nMedicamento: ").append(med.getMedicationName())
                    .append("\nDia: ").append(med.getMedicationDay())
                    .append("\nHorário").append(med.getMedicationTime())
                    .append("\n\n");
        }

        telegramBotMed.sendMessage(String.valueOf(chatId), messageBuilder.toString());
    }


}
