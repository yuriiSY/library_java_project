package com.yuriisykal.library.exeption.duplicateExeption;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
@Data
public class DuplicateEntity {
    private final String message;
    private final HttpStatus httpStatus;
    private final ZonedDateTime zonedDateTime;
}
