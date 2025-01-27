package com.med.scheduling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedsRequestDTO {

    private String chatId;

    private String medicationDay;

    private LocalTime medicationTime;

    private String medicationName;
}
