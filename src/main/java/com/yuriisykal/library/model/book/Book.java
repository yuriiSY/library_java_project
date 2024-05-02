package com.yuriisykal.library.model.book;

import com.yuriisykal.library.model.author.Author;
import jakarta.persistence.*;
import lombok.*;
import lombok.Builder;

@Entity
@Builder
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String title;
    @Column
    private int year_published;
    @Column
    private String genre;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name="author_id")
    private Author author;


}
