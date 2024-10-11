//package com.med.scheduling.service;
//
//import com.med.scheduling.MedSchedulingProperties;
//import lombok.RequiredArgsConstructor;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class MessageReceiver {
//
//    private final TelegramBotService telegramBot;
//    private final MedSchedulingProperties properties;
//
//
//    @RabbitListener(queues = "${rabbitMq.queue.name}")
//    public void receiveMessage(String message){
//        var chatId = extractChatIdFromMessage(message);
//        var messageText = extractReminderTextFromMessage(message);
//        telegramBot.sendMessage(chatId, messageText);
//    }
//
//    private String extractChatIdFromMessage(String message){
//        String[] parts = message.split(":");
//        return parts[0];
//    }
//
//    private String extractReminderTextFromMessage(String message){
//        String[] parts = message.split(":");
//        return parts[1].trim();
//    }
//}
