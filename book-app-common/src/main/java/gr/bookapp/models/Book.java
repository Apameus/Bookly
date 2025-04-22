package gr.bookapp.models;

import java.time.Instant;
import java.util.List;

public record Book(Long id, String name, List<String> authors, double price, Instant releaseDate, List<String> tags) {

    public Book(String name, List<String> authors, double price, Instant releaseDate, List<String> tags){
        this(null, name, authors, price, releaseDate, tags);
    }

    public Book withPrice(double updatedPrice) {
        return new Book(id, name, authors, updatedPrice, releaseDate, tags);
    }

    public Book withID(long setID, Book book) {
        return new Book(setID, book.name(), book.authors, book.price(), book.releaseDate, book.tags);
    }
}
