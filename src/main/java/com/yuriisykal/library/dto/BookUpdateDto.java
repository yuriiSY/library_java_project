package com.yuriisykal.library.dto;

import com.yuriisykal.library.model.author.Author;
import lombok.Data;

@Data
public class BookUpdateDto {
    private String title;
    private int year_published;
    private String genre;
    private Author author;
}
