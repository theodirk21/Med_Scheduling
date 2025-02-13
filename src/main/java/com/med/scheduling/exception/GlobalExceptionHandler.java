package com.med.scheduling.exception;

import com.med.scheduling.dto.ErroControllerResponse;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErroControllerResponse> handleNotFoundException(NotFoundException ex) {
        ErroControllerResponse err = ErroControllerResponse.builder()
                .description(NOT_FOUND.getReasonPhrase())
                .build();

        return ResponseEntity.status(NOT_FOUND).body(err);
    }

    @ExceptionHandler(ApplicationException.class)
    @ResponseBody
    public ResponseEntity<ErroControllerResponse> handleApplicationException(ApplicationException e) {
        ErroControllerResponse err = ErroControllerResponse.builder()
                .description(e.getMessage())
                .details(e.getDetails())
                .build();

        return ResponseEntity.status(e.getStatusCode()).body(err);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ErroControllerResponse> handleApplicationException(InvalidDataAccessApiUsageException e) {

        ErroControllerResponse err = ErroControllerResponse.builder()
                .description(e.getMessage())
                .build();

        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(err);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroControllerResponse> handleApplicationException(HttpMessageNotReadableException e) {

        ErroControllerResponse err = ErroControllerResponse.builder()
                .description(e.getMessage())
                .build();

        return ResponseEntity.status(BAD_REQUEST).body(err);
    }
}