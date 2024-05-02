package com.yuriisykal.library.repositories;


import com.yuriisykal.library.model.author.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author,Long> {
    boolean existsByName(String name);
}
