package com.yuriisykal.library.services;

import com.yuriisykal.library.dto.AuthorDetailsDto;
import com.yuriisykal.library.dto.AuthorDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AuthorService {


    ResponseEntity<AuthorDetailsDto> updateAuthor(Long id, AuthorDto newAuthorData);
    ResponseEntity<List<AuthorDetailsDto>> getAllAuthors();
    void deleteAuthorById(Long id);
    ResponseEntity<AuthorDetailsDto> addAuthor(AuthorDto authorDto);
}
