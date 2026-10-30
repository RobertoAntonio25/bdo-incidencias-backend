package com.bdo.incidencias.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

import com.bdo.incidencias.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Manejo de 404(Recurso no encontrado)
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Manejo de 409 (Conflicto de negocio: incidencia CERRADA no modificable)
    @ExceptionHandler(ConflictoNegocioException.class)
    public ResponseEntity<ErrorResponse> manejarConflictoNegocio(ConflictoNegocioException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // Manejo de 400 (Errores de validación)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex) {
        List<String> detalles = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            detalles.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Error en la validación de los datos enviados.",
                detalles);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Manejo de 500 (Error interno del servidor)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarErrorInterno(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ocurrió un error inesperado en el servidor.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
