package com.yuriisykal.library.services;

import com.yuriisykal.library.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {
    ResponseEntity<DetailsBookDto> getBook(Long id);

    ResponseEntity<BookShortDto> addBook(BookDto bookDto);

    HttpStatus deleteBookById(Long id);

    ResponseEntity<UpdatedBookDto> updateBook(Long id, BookUpdateDto newBookData);

    ResponseEntity<PageResponseDto> findAll(PageDto pageDto);

    byte[] generateCsvReport(PageDto pageDto);

    ResponseEntity<ImportResultDto> saveDataFromJsonFile(MultipartFile file);
}
