package com.yuriisykal.library.controlers;

import com.yuriisykal.library.dto.*;
import com.yuriisykal.library.services.BookServiceImpl;
import com.yuriisykal.library.utils.validation.Validation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class BookController {

    private BookServiceImpl bookServiceImpl;

    @GetMapping("/books/{id}")
    public ResponseEntity<DetailsBookDto> getBook(@PathVariable Long id) {
       return bookServiceImpl.getBook(id);
    }

    @PostMapping("/books")
    public ResponseEntity<BookShortDto> addBook(@RequestBody @Validated BookDto bookDto, BindingResult bindingResult) {
        Validation.validationFields(bindingResult);
        return bookServiceImpl.addBook(bookDto);
    }

    @DeleteMapping("/books/{id}")
    public HttpStatus deleteBook(@PathVariable Long id) {
       return bookServiceImpl.deleteBookById(id);
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<UpdatedBookDto> updateBook(@PathVariable Long id, @RequestBody BookUpdateDto bookUpdateDto) {
        return bookServiceImpl.updateBook(id,bookUpdateDto);
    }

    @PostMapping("/books/_list")
    public ResponseEntity<PageResponseDto> getBooksListByAuthor(@RequestBody PageDto pageDto) {
        return bookServiceImpl.findAll(pageDto);
    }


    @PostMapping("/books/_report")
    public void generateReport(@RequestBody PageDto pageDto, HttpServletResponse httpServletResponse) throws IOException {

        byte[] reportBytes = bookServiceImpl.generateCsvReport(pageDto);

        String fileName = "report.csv";
        httpServletResponse.setContentType("text/csv");
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+fileName+"\"");
        httpServletResponse.setContentLength(reportBytes.length);
        httpServletResponse.getOutputStream().write(reportBytes);
        httpServletResponse.getOutputStream().flush();

    }

    @PostMapping("/books/import")
    public ResponseEntity<ImportResultDto> importEntities(@RequestBody MultipartFile file) {
        return bookServiceImpl.saveDataFromJsonFile(file);
    }

}
