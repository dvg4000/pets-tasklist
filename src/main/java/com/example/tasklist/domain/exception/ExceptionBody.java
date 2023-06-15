package com.example.tasklist.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ExceptionBody {

    private final String message;
    private final Map<String, String> errors;

    public ExceptionBody(String message) {
        this.message = message;
        this.errors = null;
    }
}
