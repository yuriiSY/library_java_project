package com.yuriisykal.library.utils.jsonParser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.yuriisykal.library.model.author.Author;
import com.yuriisykal.library.model.book.Book;
import com.fasterxml.jackson.core.JsonParseException;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONParser {

    public Map<ImportResulFilter, List<Book>> parseJsonFile(byte[] fileBytes) throws JsonParseException {
        List<Book> successBooks = new ArrayList<>();
        List<Book> wrongBooks = new ArrayList<>();
        Map<ImportResulFilter, List<Book>> filteredBooks = new HashMap<>();

        JsonFactory jsonFactory = new JsonFactory();

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);
             JsonParser jsonParser = jsonFactory.createParser(inputStream)) {
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                Book book = parseBook(jsonParser);
                if (isValidBook(book)) {
                    successBooks.add(book);
                } else {
                    wrongBooks.add(book);
                }
            }
        } catch (IOException e) {
            throw new JsonParseException("Error reading file: " + e.getMessage());
        }

        filteredBooks.put(ImportResulFilter.SUCCESS, successBooks);
        filteredBooks.put(ImportResulFilter.WRONG, wrongBooks);

        return filteredBooks;
    }

    private Book parseBook(JsonParser jsonParser) throws IOException {
        Book book = new Book();
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jsonParser.getCurrentName();
            if (fieldName == null) {
                continue;
            }
            switch (fieldName) {
                case "title":
                    jsonParser.nextToken();
                    book.setTitle(jsonParser.getValueAsString());
                    break;
                case "year_published":
                    jsonParser.nextToken();
                    book.setYear_published(jsonParser.getValueAsInt());
                    break;
                case "genre":
                    jsonParser.nextToken();
                    book.setGenre(jsonParser.getValueAsString());
                    break;
                case "author":
                    jsonParser.nextToken();
                    while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                        String authorFieldName = jsonParser.getCurrentName();
                        if ("name".equals(authorFieldName)) {
                            jsonParser.nextToken();
                            Author author = new Author();
                            author.setName(jsonParser.getValueAsString());
                            book.setAuthor(author);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return book;
    }

    private boolean isValidBook(Book book) {
        return book != null && book.getTitle() != null && !book.getTitle().isEmpty() &&
                book.getYear_published() > 0 && book.getGenre() != null && !book.getGenre().isEmpty();
    }
}
