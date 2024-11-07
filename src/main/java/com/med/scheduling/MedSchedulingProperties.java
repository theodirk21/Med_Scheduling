package com.med.scheduling;



import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@RequiredArgsConstructor
public class MedSchedulingProperties {

    @Value(value = "${telegram.bot.token}")
    private String botToken;

    @Value(value = "${telegram.bot.name}")
    private String botName;
}
