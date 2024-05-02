package com.yuriisykal.library.repositories;

import com.yuriisykal.library.model.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {

    Page<Book> findAll(Specification<Book> specification, Pageable pageable);

    List<Book> findAll(Specification<Book> specification);
}
