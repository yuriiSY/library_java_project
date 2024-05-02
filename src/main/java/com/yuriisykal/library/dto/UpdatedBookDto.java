package com.yuriisykal.library.dto;

import com.yuriisykal.library.model.author.Author;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatedBookDto {
    private Long id;
    private String title;
    private int year_published;
    private String genre;
    private Author author;
}
