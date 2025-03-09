package gr.bookapp.models;

import java.util.List;

public record Book(long id, String name, List<String> author, double price, long releaseDate, List<String> tags) {

}
