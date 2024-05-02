package com.yuriisykal.library.controlers;

import com.yuriisykal.library.dto.AuthorDetailsDto;
import com.yuriisykal.library.dto.AuthorDto;
import com.yuriisykal.library.services.AuthorServiceImpl;
import com.yuriisykal.library.utils.validation.Validation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class AuthorController {

    private AuthorServiceImpl authorServiceImpl;

    @GetMapping("/author")
    public ResponseEntity<List<AuthorDetailsDto>> getAuthors() {
        return authorServiceImpl.getAllAuthors();
    }

    @PostMapping("/author")
    public ResponseEntity<AuthorDetailsDto> addAuthor(@RequestBody @Validated AuthorDto authorDto, BindingResult bindingResult) {
        Validation.validationFields(bindingResult);
        return authorServiceImpl.addAuthor(authorDto);
    }

    @DeleteMapping("/author/{id}")
    public HttpStatus deleteAuthor(@PathVariable Long id) {
        authorServiceImpl.deleteAuthorById(id);
        return HttpStatus.OK;
    }

    @PutMapping("/author/{id}")
    public ResponseEntity<AuthorDetailsDto> updateAuthor(@PathVariable Long id, @RequestBody @Validated AuthorDto authorDto, BindingResult bindingResult) {
        Validation.validationFields(bindingResult);
        return authorServiceImpl.updateAuthor(id,authorDto);
    }

}
