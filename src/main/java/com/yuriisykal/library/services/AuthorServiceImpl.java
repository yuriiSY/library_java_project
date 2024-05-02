package com.yuriisykal.library.services;

import com.yuriisykal.library.dto.AuthorDetailsDto;
import com.yuriisykal.library.dto.AuthorDto;
import com.yuriisykal.library.exeption.duplicateExeption.DuplicateException;
import com.yuriisykal.library.exeption.objectNotExistExeption.ObjectNotExistException;
import com.yuriisykal.library.model.author.Author;
import com.yuriisykal.library.repositories.AuthorRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private AuthorRepository authorRepository;
    @Override
    public ResponseEntity<AuthorDetailsDto> updateAuthor(Long id, AuthorDto newAuthorData) {
        if (!authorRepository.existsByName(newAuthorData.getName())) {
            Optional<Author> authorOptional = authorRepository.findById(id);
            if (authorOptional.isPresent() && newAuthorData != null) {
                Author author = authorOptional.get();
                author.setName(newAuthorData.getName());
                Author updatedAuthor = authorRepository.save(author);
                return new ResponseEntity<>(toAuthorDetailsDto(updatedAuthor), HttpStatus.OK);
            } else {
                throw new ObjectNotExistException("Author not exist");
            }
        } else {
            throw new DuplicateException("Author with the same name already exists");
        }
    }
    @Override
    public ResponseEntity<List<AuthorDetailsDto>> getAllAuthors() {
        List<AuthorDetailsDto> authorDetailsDto = authorRepository.findAll().stream()
                .map(this::toAuthorDetailsDto)
                .toList();
        return new ResponseEntity<>(authorDetailsDto,HttpStatus.OK);
    }
    @Override
    public void deleteAuthorById(Long id) {
        authorRepository.deleteById(id);
    }
    @Override
    public ResponseEntity<AuthorDetailsDto> addAuthor(AuthorDto authorDto) {
        if (authorRepository.existsByName(authorDto.getName())) {
            throw new DuplicateException("Author with the same name already exists");
        }
        Author author = Author.builder().name(authorDto.getName()).build();

        return new ResponseEntity<>(toAuthorDetailsDto(authorRepository.save(author)),HttpStatus.CREATED);
    }

    public Author findOrCreateNew(Author author) {
        if (author.getId() != null){
            Optional<Author> personOptional = authorRepository.findById(author.getId());
            if (personOptional.isPresent()) {
                return personOptional.get();
            } else {
                throw new ObjectNotExistException("No author with such id");
            }
        } else {
                if (!authorRepository.existsByName(author.getName())) {
                    return authorRepository.save(author);
                } else {
                    throw new DuplicateException("Author with such name already exist");
                }
            }
    }

    private AuthorDetailsDto toAuthorDetailsDto(Author data) {
        return AuthorDetailsDto
                .builder()
                .id(data.getId())
                .name(data.getName())
                .build();
    }

}
