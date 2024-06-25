package com.yuriisykal.library.services;

import com.yuriisykal.library.dto.*;
import com.yuriisykal.library.exeption.invalidFileExeption.InvalidFileException;
import com.yuriisykal.library.exeption.objectNotExistExeption.ObjectNotExistException;
import com.yuriisykal.library.exeption.validationExeption.FieldsValidationException;
import com.yuriisykal.library.message.EmailDTO;
import com.yuriisykal.library.model.book.Book;
import com.yuriisykal.library.model.author.Author;
import com.yuriisykal.library.repositories.BookRepository;
import com.yuriisykal.library.services.specification.BookSpec;
import com.yuriisykal.library.utils.jsonParser.ImportResulFilter;
import com.yuriisykal.library.utils.jsonParser.JSONParser;
import com.yuriisykal.library.utils.validation.Validation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;
    private AuthorServiceImpl authorServiceImpl;
    private KafkaMessageSenderService kafkaMessageSenderService;
    @Override
    public ResponseEntity<DetailsBookDto> getBook(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            DetailsBookDto detailsBookDto = toDetailsBookDto(book);
            return new ResponseEntity<>(detailsBookDto,HttpStatus.OK);
        } else {
            throw new ObjectNotExistException("Book not found");
        }
    }

    @Override
    public ResponseEntity<BookShortDto> addBook(BookDto bookDto) {
        Book book = Book.builder()
                        .title(bookDto.getTitle())
                        .genre(bookDto.getGenre())
                        .year_published(bookDto.getYear_published())
                .build();
        if(bookDto.getAuthor() != null) {
            Author author = authorServiceImpl.findOrCreateNew(bookDto.getAuthor());
            book.setAuthor(author);
        }
        BookShortDto savedBookDto = toBookShortDto(bookRepository.save(book));
        EmailDTO emailDTO = EmailDTO
                .builder()
                .recipient("test@gmail.com")
                .subject("Test Subject")
                .text("Test Content")
                .build();
        kafkaMessageSenderService.sendMessage(emailDTO);
        return  new ResponseEntity<>(savedBookDto, HttpStatus.CREATED);
    }

    @Override
    public HttpStatus deleteBookById(Long id) {
        bookRepository.deleteById(id);
        return HttpStatus.OK;
    }

    @Override
    public ResponseEntity<UpdatedBookDto> updateBook(Long id, BookUpdateDto newBookData) {
        if (Validation.hasAtLeastOneParameter(newBookData)) {
            Optional<Book> bookOptional = bookRepository.findById(id);
            if (bookOptional.isPresent()) {
                Book book = bookOptional.get();
                book.setAuthor(newBookData.getAuthor());
                book.setTitle(newBookData.getTitle());
                book.setYear_published(newBookData.getYear_published());
                book.setGenre(newBookData.getGenre());
                Book updatedBook = bookRepository.save(book);
                return mappingResponseUpdatedBookDto(toUpdatedBookDto(updatedBook));
            } else {
            throw new ObjectNotExistException("Book not found");
            }
        }else {
            throw new FieldsValidationException("Minimum 1 field is required");
        }

    }

    @Override
    public ResponseEntity<PageResponseDto> findAll(PageDto pageDto) {
        int size = pageDto.getSize() != 0 ? pageDto.getSize() : 10;
        int page = pageDto.getPage();
        Specification<Book> specification = BookSpec.searchByCriteria(pageDto.getAuthorId(), pageDto);
        Page<Book> pages = bookRepository.findAll(specification, PageRequest.of(page, size));
        long totalPages = pages.getTotalPages();
        List<BookShortDto> books = pages.getContent().stream()
               .map(this::toBookShortDto)
               .toList();
        return new ResponseEntity<>(new PageResponseDto(books,totalPages),HttpStatus.OK);
    }

    @Override
    public byte[] generateCsvReport(PageDto pageDto) {

        Specification<Book> specification = BookSpec.searchByCriteria(pageDto.getAuthorId(), pageDto);

        List<Book> books = bookRepository.findAll(specification);

        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Title,Year Published,Genre,Author\n");
        for (Book book : books) {
            csvContent.append(String.format("%s,%d,%s,%s\n",
                    book.getTitle(),
                    book.getYear_published(),
                    book.getGenre(),
                    book.getAuthor().getName()));
        }

        return csvContent.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public ResponseEntity<ImportResultDto> saveDataFromJsonFile(MultipartFile file) {
        Map<ImportResulFilter,List<Book>> filteredBooks;
        String originalFilename = file.getOriginalFilename();
        String fileFormat = ".json";
        if (originalFilename != null && originalFilename.contains(fileFormat)) {
            try {
                filteredBooks = filterByAuthor(JSONParser.parseJsonFile(file.getBytes()));
                bookRepository.saveAll(filteredBooks.get(ImportResulFilter.SUCCESS));
                return new ResponseEntity<>(new ImportResultDto(filteredBooks.get(ImportResulFilter.SUCCESS).size(),filteredBooks.get(ImportResulFilter.WRONG).size()), HttpStatus.OK);
            } catch (IOException e) {
                throw new InvalidFileException("Wrong file");
            }
        } else {
            throw new InvalidFileException("File with wrong format");
        }
  }


    private BookShortDto toBookShortDto(Book data) {
        return BookShortDto.builder()
                .id(data.getId())
                .title(data.getTitle())
                .build();
    }

    private UpdatedBookDto toUpdatedBookDto(Book data) {
        return UpdatedBookDto
                .builder()
                .id(data.getId())
                .title(data.getTitle())
                .genre(data.getGenre())
                .year_published(data.getYear_published())
                .author(data.getAuthor())
                .build();
    }
    private DetailsBookDto toDetailsBookDto(Book data) {
        return DetailsBookDto
                .builder()
                .id(data.getId())
                .title(data.getTitle())
                .genre(data.getGenre())
                .year_published(data.getYear_published())
                .author(data.getAuthor())
                .build();
    }

    private ResponseEntity<UpdatedBookDto> mappingResponseUpdatedBookDto(UpdatedBookDto updatedBookDto) {
        return new ResponseEntity<>(updatedBookDto, HttpStatus.OK);
    }


    private Map<ImportResulFilter, List<Book>> filterByAuthor(Map<ImportResulFilter, List<Book>> map) {
        List<Book> successBooks = map.get(ImportResulFilter.SUCCESS);
        List<Book> wrongBooks = map.get(ImportResulFilter.WRONG);
        List<Book> failByAuthorBooks = new ArrayList<>();

        for (Book book : successBooks) {
            if (book.getAuthor() != null && book.getAuthor().getId() != null ) {
                Optional<Author> author = authorServiceImpl.findById(book.getAuthor().getId());
                if (author.isEmpty()) {
                    failByAuthorBooks.add(book);
                } else {
                    book.setAuthor(author.get());
                }
            }
        }

        successBooks.removeAll(failByAuthorBooks);
        wrongBooks.addAll(failByAuthorBooks);
        return map;
    }



}
