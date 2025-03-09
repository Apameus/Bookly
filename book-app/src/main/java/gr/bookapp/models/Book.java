package gr.bookapp.models;

import java.util.List;

public record Book(long id, String name, String author, double price, long date, List<String> tags) {

}
