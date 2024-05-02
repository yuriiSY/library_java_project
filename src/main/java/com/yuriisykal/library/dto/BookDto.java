package com.yuriisykal.library.dto;


import com.yuriisykal.library.model.author.Author;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookDto {
    @NotBlank(message = "Title is required")
    private String title;
    @NotNull(message = "Year Published is required")
    private int year_published;
    @NotBlank(message = "Genre is required")
    private String genre;
    private Author author;
}
