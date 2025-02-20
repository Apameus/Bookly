package model;

import java.time.LocalDate;
import java.util.List;

public record Book(long id, String name, String author, List<String> tags, double price, LocalDate date) {
}
