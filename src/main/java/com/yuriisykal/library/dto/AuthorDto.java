package com.yuriisykal.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthorDto {
    @NotBlank
    private String name;
}
