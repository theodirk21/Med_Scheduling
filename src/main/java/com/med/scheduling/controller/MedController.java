package com.med.scheduling.controller;

import com.med.scheduling.dto.*;

import com.med.scheduling.service.MedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "med-verification", description = "Controle de salvamentos no banco de dados")
public class MedController {

    private final MedService medSerice;

    @Operation(
            summary = "Lista de medicamentos cadastrados na base", //
            description = "Lista todos os medicamentos cadastrados na base", //
            tags = "med-verification"
    )
    @GetMapping("/")
    @ApiResponses(value = { //
            @ApiResponse(responseCode = "200", description = "Lista de medicamentos cadastrados na base"),
            @ApiResponse(responseCode = "404", description = "Lista não encontrada!", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErroControllerResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro do servidor!", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErroControllerResponse.class)
            ))
    })
    public ResponseEntity<List<MedsResponseDTO>> findAllMeds() {
            List<MedsResponseDTO> allMeds = medSerice.findAllMeds();
            return ResponseEntity.ok(allMeds);
    }

    @Operation(
            summary = "Lista medicamentos pelo filtro usado", //
            description = "Lista medicamentos pelo filtro usado", //
            tags = "med-verification"
    )
    @GetMapping("/filter/")
    @ApiResponses(value = { //
            @ApiResponse(responseCode = "200", description = "Lista medicamentos pelo filtro usado"), //
            @ApiResponse(responseCode = "404", description = "Filtro não retornou nada", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErroControllerResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro do servidor!", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErroControllerResponse.class)
            )),
    })
    public ResponseEntity<List<MedsResponseDTO>> findMedsByFilter(
            @PageableDefault Pageable page,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String chatId,
            @RequestParam(required = false) String medicationDay,
            @RequestParam(required = false) LocalTime medicationTime,
            @RequestParam(required = false) String medicationName

            ) {

        var paramsFilter = new MedsFilterDTO(id, chatId, medicationDay, medicationTime, medicationName);

        return ResponseEntity.ok(medSerice.findMedsByFilter(page, paramsFilter));
    }


    @Operation(
            summary = "Deletar medicameto pelo chatid e nome ", //
            description = "Deletar medicameto pelo chatid e nome ", //
            tags = "med-verification"
    )
    @DeleteMapping("/{id}")
    @ApiResponses(value = { //
            @ApiResponse(responseCode = "204", description = "Deleta medicameto pelo chatid e nome "), //
            @ApiResponse(responseCode = "404", description = "Item á ser excluido não encontrado", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErroControllerResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro do servidor!", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErroControllerResponse.class)
            )),
    })
    public ResponseEntity<Void> deleteMed(
            @PathVariable("id") Long id

    ) {
        medSerice.deleteMed(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Cria medicamento na base", //
            description = "Cria medicamento na base", //
            tags = "med-verification"
    )
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @ApiResponses(value = { //
            @ApiResponse(responseCode = "201", description = "Cria medicamento na base", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = MedsResponseIdDTO.class)
            )),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou ausentes no corpo da requisição", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErroControllerResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro do servidor!", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErroControllerResponse.class)
            )),
    })
    public ResponseEntity<MedsResponseIdDTO> createMed(
            @RequestBody MedsRequestDTO medsRequestDTO
    ) {
        MedsResponseIdDTO response = medSerice.createMed(medsRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @Operation(
            summary = "Atualiza medicamento na base", //
            description = "Atualiza medicamento na base", //
            tags = "med-verification"
    )
    @PatchMapping(path = "/{id}",consumes = APPLICATION_JSON_VALUE)
    @ApiResponses(value = { //
            @ApiResponse(responseCode = "200", description = "Atualiza medicamento na base" ), //
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou ausentes no corpo da requisição", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErroControllerResponse.class)
            )),
            @ApiResponse(responseCode = "404", description = "Medicamento para atualização não encontrado", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErroControllerResponse.class)
            )),
            @ApiResponse(responseCode = "500", description = "Erro do servidor!", content = @Content(
                    mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErroControllerResponse.class)
            )),
    })
    public ResponseEntity<MedsResponseDTO> updateMed(
            @RequestBody  MedsRequestDTO medsRequestDTO,
            @PathVariable("id") Long id
    ) {
            MedsResponseDTO body = medSerice.updateMed(medsRequestDTO, id);
            return ResponseEntity.ok(body);
    }
}
