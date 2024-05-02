package com.yuriisykal.library.utils.validation;

import com.yuriisykal.library.dto.BookUpdateDto;
import com.yuriisykal.library.exeption.validationExeption.FieldsValidationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

public class Validation {
    public static boolean hasAtLeastOneParameter(BookUpdateDto bookUpdateDto) {
        return bookUpdateDto.getTitle() != null ||
                bookUpdateDto.getGenre() != null ||
                bookUpdateDto.getYear_published() > 0 ||
                bookUpdateDto.getAuthor() != null;
    }

    public static void validationFields(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> missingFieldsWithMessages = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                missingFieldsWithMessages.put(error.getField(), error.getDefaultMessage());
            }
            throw new FieldsValidationException("Validation Failed", missingFieldsWithMessages);
        }
    }
}
