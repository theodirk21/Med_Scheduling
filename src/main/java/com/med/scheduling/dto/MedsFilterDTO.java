package com.med.scheduling.dto;

import java.time.LocalTime;

public record MedsFilterDTO (
    Long id,
    String chatId,
    String medicationDay,
    LocalTime medicationTime,
    String medicationName) {

}

