package com.yuriisykal.library.exeption.validationExeption;

import lombok.Getter;

import java.util.Map;
@Getter
public class FieldsValidationException extends RuntimeException {

    private Map<String, String> missingFields;

    public FieldsValidationException(String message, Map<String, String> missingFields) {
        super(message);
        this.missingFields = missingFields;
    }

    public FieldsValidationException(String message) {
        super(message);
    }
}
