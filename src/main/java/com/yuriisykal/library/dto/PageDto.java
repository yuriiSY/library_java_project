package com.yuriisykal.library.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageDto {
    @NotNull
    private Long id;
    private String title;
    private int year_published;
    private String genre;
    private int page;
    private int size;
}
