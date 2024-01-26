package it.almaviva.difesa.template.shared.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RestControllerAdvice
@Slf4j
public class ExceptionHandlingControllerAdvice extends ResponseEntityExceptionHandler {

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
    private static final String DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleAnyUncaughtException(Exception ex) {
        log.error(ex.getMessage());

        CustomErrorResponse customErrorResponse = new CustomErrorResponse();
        customErrorResponse.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DD_MM_YYYY_HH_MM_SS)));
        customErrorResponse.setMessage(INTERNAL_SERVER_ERROR_MESSAGE);
        customErrorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(customErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<CustomErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse();
        customErrorResponse.setMessage(ex.getReason());
        customErrorResponse.setStatus(ex.getStatus().value());
        return new ResponseEntity<>(customErrorResponse, ex.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public void handleConstraintViolationException(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            customErrorResponse.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DD_MM_YYYY_HH_MM_SS)));
            customErrorResponse.setMessage(error.getDefaultMessage());
            customErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleEntityNotFound(EntityNotFoundException e) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse();
        customErrorResponse.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DD_MM_YYYY_HH_MM_SS)));
        customErrorResponse.setMessage(e.getMessage());
        customErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(customErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<CustomErrorResponse> handleWebClientException(WebClientResponseException e) {
        CustomErrorResponse customErrorResponse = new CustomErrorResponse();
        customErrorResponse.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DD_MM_YYYY_HH_MM_SS)));
        var message = e.getResponseBodyAsString();
        int startIndex = StringUtils.ordinalIndexOf(message, "\"", 7)+1;
        int endIndex = StringUtils.ordinalIndexOf(message, ",", 2)-1;
        customErrorResponse.setMessage(message.substring(startIndex, endIndex));
        customErrorResponse.setStatus(e.getStatusCode().value());
        return new ResponseEntity<>(customErrorResponse, e.getStatusCode());
    }
}