package com.yuriisykal.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImportResultDto {
    private long successfullyImported;
    private long notImported;
}
