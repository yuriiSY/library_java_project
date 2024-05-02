package com.yuriisykal.library.exeption.validationExeption;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.Map;

@Data
public class FieldValidationWithFields {
    private final String message;
    private final Map<String, String> missedFields;
    private final HttpStatus httpStatus;
    private final ZonedDateTime zonedDateTime;
}
