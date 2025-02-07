package com.med.scheduling.repository.projection;

import java.time.LocalTime;

public interface CustomScheduleMed {


Long getId();

String getChatId();

String getMedicationDay();

LocalTime getMedicationTime();

String getMedicationName();

}
