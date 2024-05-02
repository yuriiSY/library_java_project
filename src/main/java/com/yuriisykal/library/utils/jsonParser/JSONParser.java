package com.yuriisykal.library.utils.jsonParser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.yuriisykal.library.model.book.Book;
import com.fasterxml.jackson.core.JsonParseException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
public class JSONParser {
    /**
     * Parses a JSON file containing book information and returns a list of books.
     *
     * @param fileBytes The JSON file to parse.
     * @return A list of books parsed from the JSON file.
     */
    public static Map<ImportResulFilter,List<Book>> parseJsonFile(byte[] fileBytes) throws JsonParseException {
        List<Book> successBooks = new ArrayList<>();
        List<Book> wrongBooks = new ArrayList<>();
        Map<ImportResulFilter,List<Book>> filteredBooks = new HashMap<>();

        JsonFactory jsonFactory = new JsonFactory();

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes);
             JsonParser jsonParser = jsonFactory.createParser(inputStream)) {
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
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
                        default:
                            break;
                    }
                }
                if (isValidBook(book)) {
                    successBooks.add(book);
                } else {
                    wrongBooks.add(book);
                }
                filteredBooks.put(ImportResulFilter.SUCCESS,successBooks);
                filteredBooks.put(ImportResulFilter.WRONG,wrongBooks);
            }
        } catch (IOException e) {
            log.error("Error parsing JSON: {}", e.getMessage());
            throw new JsonParseException("Error reading file: " + e.getMessage());
        }
        return filteredBooks;
    }

    private static boolean isValidBook(Book book) {
        return book != null && book.getTitle() != null && !book.getTitle().isEmpty() &&
                book.getYear_published() > 0 && book.getGenre() != null && !book.getGenre().isEmpty();
    }
}
