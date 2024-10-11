package com.med.scheduling.models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Entity
@Data
@Table(name = "Schedule_med")
@Builder
public class ScheduleMed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String chatId;

    @Column(nullable = false)
    private String medicationDay;

    @Column(nullable = false)
    private LocalTime medicationTime;

    @Column(nullable = false)
    private String medicationName;
}
