package com.yohanSald.api.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ErrorResponse {

    private int status;
    private String erreur;
    private String message;
    private Map<String, String> details;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String erreur, String message) {
        this.status    = status;
        this.erreur    = erreur;
        this.message   = message;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String erreur, String message, Map<String, String> details) {
        this(status, erreur, message);
        this.details = details;
    }
}