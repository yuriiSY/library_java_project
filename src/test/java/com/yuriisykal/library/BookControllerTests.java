package com.yuriisykal.library;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuriisykal.library.dto.*;
import com.yuriisykal.library.exeption.validationExeption.FieldsValidation;
import com.yuriisykal.library.model.book.Book;
import com.yuriisykal.library.model.author.Author;
import com.yuriisykal.library.repositories.AuthorRepository;
import com.yuriisykal.library.repositories.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.MOCK,
		classes = LibraryApplication.class)
@AutoConfigureMockMvc
class BookControllerTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String FIELDS_VALIDATION_FAILED = "Validation Failed";
	private static final String CREATE_BOOK_ENDPOINT = "/api/books";
	private static final String GET_BOOK_ENDPOINT = "/api/books";

	@AfterEach
	public void afterEach() {
		bookRepository.deleteAll();
		authorRepository.deleteAll();
	}

	@Test
	public void testCreateBook() throws Exception {
		String title = "Title";
		String genre = "genre";
		int year_published = 1982;
		String body = """
          {
              "title": "%s",
              "genre": "%s",
              "year_published": %d
          }               
        """.formatted(title, genre, year_published);

		MvcResult mvcResult = mvc.perform(post(CREATE_BOOK_ENDPOINT)
						.contentType(MediaType.APPLICATION_JSON)
						.content(body)
				)
				.andExpect(status().isCreated())
				.andReturn();


		BookShortDto bookDto = parseResponse(mvcResult,BookShortDto.class);

		int bookID = (int) bookDto.getId();

		assertThat(bookID).isGreaterThanOrEqualTo(1);
		Optional<Book> book = bookRepository.findById(bookDto.getId());

		assertThat(book).isPresent();
		assertThat(book.get().getTitle()).isEqualTo(title);
		assertThat(book.get().getGenre()).isEqualTo(genre);
		assertThat(book.get().getYear_published()).isEqualTo(year_published);
	}

	@Test
	public void testCreateStudent_validation() throws Exception {
		MvcResult mvcResult = mvc.perform(post(CREATE_BOOK_ENDPOINT)
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}")
				)
				.andExpect(status().isBadRequest())
				.andReturn();

		FieldsValidation fieldsValidationException = parseResponse(mvcResult, FieldsValidation.class);
		assertThat(fieldsValidationException).isNotNull();
		assertThat(fieldsValidationException.getMessage()).isNotNull();
		assertThat(fieldsValidationException.getZonedDateTime()).isNotNull();
		assertThat(fieldsValidationException.getHttpStatus()).isNotNull();
		assertThat(fieldsValidationException.getMessage()).isEqualTo(FIELDS_VALIDATION_FAILED);
	}

	@Test
	public void testGetBook() throws Exception {
		String title = "Title";
		String genre = "genre";
		int year_published = 1982;
		Author author = Author.builder().name("Name").build();
		Book book = Book.builder().title(title).year_published(year_published).author(author).genre(genre).build();
		book = bookRepository.save(book);

		MvcResult mvcResult1 = mvc.perform(get(GET_BOOK_ENDPOINT+"/"+book.getId()))
				.andExpect(status().isOk())
				.andReturn();

		DetailsBookDto detailsBookDto = parseResponse(mvcResult1, DetailsBookDto.class);

		assertThat(detailsBookDto).isNotNull();
		assertThat(detailsBookDto.getTitle()).isEqualTo(title);
		assertThat(detailsBookDto.getGenre()).isEqualTo(genre);
		assertThat(detailsBookDto.getYear_published()).isEqualTo(year_published);
		assertThat(detailsBookDto.getAuthor().getName()).isEqualTo(author.getName());
	}

	@Test
	public void testUpdateBook() throws Exception {
		String title = "Title";
		String genre = "genre";
		int year_published = 1982;
		Author author = Author.builder().name("Name").build();
		Book book = Book.builder().title(title).year_published(year_published).author(author).genre(genre).build();
		book = bookRepository.save(book);

		String updateTitle = "UpdatedTitle";
		String updateGenre = "UpdatedGenre";
		int updateYear_published = 2000;
		String updatedAuthorName = "updatedName";
		String body = """
          {
              "title": "%s",
              "genre": "%s",
              "year_published": %d,
              "author": {
              		"name":"%s"
              }
          }               
        """.formatted(updateTitle, updateGenre, updateYear_published, updatedAuthorName);

		MvcResult mvcResultUpdate = mvc.perform(put(GET_BOOK_ENDPOINT+"/"+book.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content(body)
				)
				.andExpect(status().isOk())
				.andReturn();


		UpdatedBookDto updatedBookDto = parseResponse(mvcResultUpdate, UpdatedBookDto.class);

		Optional<Book> updatedBook = bookRepository.findById(updatedBookDto.getId());

		assertThat(updatedBook).isPresent();
		assertThat(updatedBook.get().getTitle()).isEqualTo(updateTitle);
		assertThat(updatedBook.get().getGenre()).isEqualTo(updateGenre);
		assertThat(updatedBook.get().getYear_published()).isEqualTo(updateYear_published);
		assertThat(updatedBook.get().getAuthor().getName()).isEqualTo(updatedAuthorName);
	}

	@Test
	public void testGetBooksListByAuthor() throws Exception {
		String title = "Title";
		String genre = "genre";
		int year_published = 1982;
		Author author = Author.builder().name("Name").build();
		Book book1 = new Book(null,title,year_published,genre,author);
		Book book2 = new Book(null,title,year_published,genre,author);
		List<Book> books = bookRepository.saveAll(List.of(book2,book1));

		Long id = books.get(0).getAuthor().getId();

		String body = """
          {
              "authorId": %d
          }               
        """.formatted(id);

		MvcResult mvcResult = mvc.perform(post("/api/books/_list")
								.contentType(MediaType.APPLICATION_JSON)
								.content(body))
						.andExpect(status().isOk())
						.andReturn();

		PageResponseDto pageResponseDto = parseResponse(mvcResult, PageResponseDto.class);

		assertThat(pageResponseDto).isNotNull();
		assertThat(pageResponseDto.getList().size()).isEqualTo(2);
		assertThat(pageResponseDto.getTotalPages()).isEqualTo(1);
	}

	@Test
	public void testGetReportBooksListByAuthor() throws Exception {
		String title = "Title";
		String genre = "genre";
		int year_published = 1982;
		Author author = Author.builder().name("Name").build();
		Book book = new Book(null,title,year_published,genre,author);
		book = bookRepository.save(book);

		String body = """
          {
              "authorId": %d
          }               
        """.formatted(book.getAuthor().getId());

		MvcResult mvcResult = mvc.perform(post("/api/books/_report")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isOk())
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, "text/csv"))
				.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.csv\""))
				.andReturn();
	}

	@Test
	public void testImportBooks() throws Exception {
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("books.json")) {

			MockMultipartFile file = new MockMultipartFile("file", "book.json", "application/json", inputStream);

			MvcResult mvcResult = mvc.perform(multipart("/api/books/upload").file(file))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andReturn();

			ImportResultDto importResultDto = parseResponse(mvcResult, ImportResultDto.class);
			List<Book> importedBooks = bookRepository.findAll();

			assertThat(importResultDto).isNotNull();
			assertThat(importedBooks.size()).isEqualTo(importResultDto.getSuccessfullyImported());
		}
	}

	private <T>T parseResponse(MvcResult mvcResult, Class<T> c) {
		try {
			return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), c);
		} catch (JsonProcessingException | UnsupportedEncodingException e) {
			throw new RuntimeException("Error parsing json", e);
		}
	}
}
