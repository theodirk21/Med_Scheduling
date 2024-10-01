package com.med.scheduling.models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Entity
@Table(name = "schedules")
@Data
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    private String chatId;

    private String day;

    private LocalTime time;

    private String medicationName;
}
