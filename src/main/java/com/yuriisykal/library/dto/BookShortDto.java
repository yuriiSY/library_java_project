package com.yuriisykal.library.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookShortDto {
    private long id;
    private String title;
}
