package com.yuriisykal.library;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuriisykal.library.dto.AuthorDetailsDto;
import com.yuriisykal.library.model.author.Author;
import com.yuriisykal.library.repositories.AuthorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = LibraryApplication.class)
@AutoConfigureMockMvc
public class AuthorControllerTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String AUTHOR_ENDPOINT = "/api/author";

    @AfterEach
    public void afterEach() {
        authorRepository.deleteAll();
    }
    @Test
    public void testCreateAuthor() throws Exception {
        String name = "Name";
        String body = """
          {
              "name": "%s"
          }               
        """.formatted(name);

        MvcResult mvcResult = mvc.perform(post(AUTHOR_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isCreated())
                .andReturn();


        AuthorDetailsDto authorDetailsDto = parseResponse(mvcResult, AuthorDetailsDto.class);

        Long authorId = authorDetailsDto.getId();

        assertThat(authorId).isGreaterThanOrEqualTo(1);
        Optional<Author> author = authorRepository.findById(authorId);

        assertThat(author).isPresent();
        assertThat(author.get().getName()).isEqualTo(name);
    }
    @Test
    public void testDeleteAuthor() throws Exception {
        String name = "Name";
        Author author = Author.builder().name(name).build();
        author = authorRepository.save(author);
        mvc.perform(delete(AUTHOR_ENDPOINT+"/"+author.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
       Optional<Author> authorDB = authorRepository.findById(author.getId());
       assertThat(authorDB).isNotPresent();
    }
    @Test
    public void testGetAllAuthors() throws Exception {
        String name = "Name";
        Author author = Author.builder().name(name).build();
        authorRepository.save(author);
        mvc.perform(get(AUTHOR_ENDPOINT))
                .andExpect(status().isOk())
                .andReturn();


    }
    @Test
    public void testUpdateAuthor() throws Exception {
        String name = "Name";
        Author author = Author.builder().name(name).build();
        author = authorRepository.save(author);

        String updateName = "UpdatedName";

        String body = """
          {
              "name": "%s"
          }               
        """.formatted(updateName);

        MvcResult mvcResultUpdate = mvc.perform(put(AUTHOR_ENDPOINT + "/" + author.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                )
                .andExpect(status().isOk())
                .andReturn();

        AuthorDetailsDto authorDetailsDto = parseResponse(mvcResultUpdate, AuthorDetailsDto.class);
        Optional<Author> updatedAuthor = authorRepository.findById(authorDetailsDto.getId());

        assertThat(updatedAuthor).isPresent();
        assertThat(updatedAuthor.get().getName()).isEqualTo(updateName);
    }


    private <T>T parseResponse(MvcResult mvcResult, Class<T> c) {
        try {
            return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), c);
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Error parsing json", e);
        }
    }
}
