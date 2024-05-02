package com.yuriisykal.library.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorDetailsDto {

    private Long id;
    private String name;

}
