package com.yuriisykal.library.exeption.objectNotExistExeption;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
public class ObjectNotExist {
    private final String message;
    private final HttpStatus httpStatus;
    private final ZonedDateTime zonedDateTime;
}
