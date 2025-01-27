package com.med.scheduling.dto;


import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedsResponseDTO extends MedsRequestDTO{

    private Long id;

}
