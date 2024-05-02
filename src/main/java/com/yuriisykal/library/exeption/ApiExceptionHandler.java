package com.yuriisykal.library.exeption;

import com.yuriisykal.library.exeption.duplicateExeption.DuplicateEntity;
import com.yuriisykal.library.exeption.duplicateExeption.DuplicateException;
import com.yuriisykal.library.exeption.invalidFileExeption.InvalidFile;
import com.yuriisykal.library.exeption.invalidFileExeption.InvalidFileException;
import com.yuriisykal.library.exeption.objectNotExistExeption.ObjectNotExist;
import com.yuriisykal.library.exeption.objectNotExistExeption.ObjectNotExistException;
import com.yuriisykal.library.exeption.validationExeption.FieldValidationWithFields;
import com.yuriisykal.library.exeption.validationExeption.FieldsValidation;
import com.yuriisykal.library.exeption.validationExeption.FieldsValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;
@ControllerAdvice
public class ApiExceptionHandler {

    private static final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;;
    private static final HttpStatus CONFLICT = HttpStatus.CONFLICT;;


    @ExceptionHandler(value = {InvalidFileException.class})
    public ResponseEntity<Object> handleInvalidPathException(InvalidFileException e) {
        InvalidFile invalidFile = new InvalidFile(e.getMessage(), BAD_REQUEST, ZonedDateTime.now());
        return new ResponseEntity<>(invalidFile, BAD_REQUEST);
    }
    @ExceptionHandler(value = {FieldsValidationException.class})
    public ResponseEntity<Object> handleFieldsValidationException(FieldsValidationException e) {
        if (e.getMissingFields() != null) {
            FieldValidationWithFields fieldValidationWithFields = new FieldValidationWithFields(e.getMessage(), e.getMissingFields(), BAD_REQUEST, ZonedDateTime.now());
            return new ResponseEntity<>(fieldValidationWithFields, BAD_REQUEST);
        } else {
            FieldsValidation fieldsValidation = new FieldsValidation(e.getMessage(),BAD_REQUEST, ZonedDateTime.now());
            return new ResponseEntity<>(fieldsValidation, BAD_REQUEST);
        }

    }
    @ExceptionHandler(value = {ObjectNotExistException.class})
    public ResponseEntity<Object> handleObjectNotExistException(ObjectNotExistException e) {
        ObjectNotExist objectNotExist = new ObjectNotExist(e.getMessage(), BAD_REQUEST, ZonedDateTime.now());
        return new ResponseEntity<>(objectNotExist, BAD_REQUEST);
    }

    @ExceptionHandler(value = {DuplicateException.class})
    public ResponseEntity<Object> handleDuplicationException(DuplicateException e) {
        DuplicateEntity duplicateEntity = new DuplicateEntity(e.getMessage(), CONFLICT, ZonedDateTime.now());
        return new ResponseEntity<>(duplicateEntity, HttpStatus.CONFLICT);
    }
}
