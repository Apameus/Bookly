package gr.bookapp.repositories;

import gr.bookapp.models.Book;

import java.time.Instant;
import java.util.List;

public interface BookRepository {
    List<Book> findBooksWithName(String name);

    List<Book> findBooksWithAuthors(List<String> authors);

    List<Book> findBooksWithTags(List<String> tags);

    List<Book> findBooksInPriceRange(double min, double max);

    List<Book> findBooksInDateRange(Instant from, Instant to);

    List<Book> getAllBooks();

    void add(Book book);

    void deleteBookByID(long bookID);

    Book getBookByID(long bookID);
}
