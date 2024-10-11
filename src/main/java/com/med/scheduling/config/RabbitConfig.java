//package com.med.scheduling.config;
//
//import com.med.scheduling.MedSchedulingProperties;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@RequiredArgsConstructor
//public class RabbitConfig {
//
//   private final MedSchedulingProperties properties;
//
//   @Bean
//    public Queue medicationQueue(){
//        return new Queue(properties.getRabbitQueueName(), false);
//    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
//       return new RabbitTemplate(connectionFactory);
//    }
//
//
//
//
//}
