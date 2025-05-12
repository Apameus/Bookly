package gr.bookapp.models;

import java.time.Instant;
import java.util.List;

public record Book(Long id, String name, List<String> authors, double price, Instant releaseDate, List<String> tags) {

    public Book(String name, List<String> authors, double price, Instant releaseDate, List<String> tags){
        this(0L, name, authors, price, releaseDate, tags);
    }

    public Book withPrice(double updatedPrice) {
        return new Book(id, name, authors, updatedPrice, releaseDate, tags);
    }

    public Book withID(long setID) {
        return new Book(setID, this.name(), this.authors, this.price(), this.releaseDate, this.tags);
    }
}
