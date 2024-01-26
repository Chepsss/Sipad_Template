package it.almaviva.difesa.template.shared.exception;

import lombok.Data;

@Data
public class CustomErrorResponse {

    private String timestamp;
    private String message;
    private int status;
}