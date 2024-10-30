package com.med.scheduling.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserState {
    private String chatId;
    private MedicationState currentStep;
    private String medicationName;
    private List<String> daysOfWeek = new ArrayList<>();
    private LocalTime time;

}

