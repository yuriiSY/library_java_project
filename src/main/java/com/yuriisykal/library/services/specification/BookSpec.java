package com.yuriisykal.library.services.specification;

import com.yuriisykal.library.dto.PageDto;
import com.yuriisykal.library.model.book.Book;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class BookSpec {

    public static Specification<Book> searchByCriteria(long personId, PageDto pageDto) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("author").get("id"), personId));

            if (pageDto.getTitle() != null && !pageDto.getTitle().isEmpty()) {
                String titlePatter = "%" + pageDto.getTitle().toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), titlePatter));
            }
            if (pageDto.getGenre() != null && !pageDto.getGenre().isEmpty()) {
                String genrePatter = "%" + pageDto.getGenre().toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("genre")), genrePatter));
            }

            return predicate;
        };
    }
}
