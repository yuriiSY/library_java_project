package com.yuriisykal.library.dto;

import com.yuriisykal.library.model.book.Book;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PageResponseDto {
    private List<BookShortDto> list;
    private long totalPages;
}
