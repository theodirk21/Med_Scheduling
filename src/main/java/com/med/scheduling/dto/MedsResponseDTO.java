package com.med.scheduling.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedsResponseDTO{

    private Long id;

    private String chatId;

    private String medicationDay;

    @Schema(example = "19:30:00")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime medicationTime;

    private String medicationName;

}
