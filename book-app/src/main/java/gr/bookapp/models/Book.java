package gr.bookapp.models;

import java.util.List;

public record Book(long id, String name, List<String> authors, double price, long releaseDate, List<String> tags) {

    public Book withPrice(double updatedPrice) {
        return new Book(id, name, authors, updatedPrice, releaseDate, tags);
    }
}
