package model;

import java.util.Date;
import java.util.List;

public record Book(Long id, String name, String author, List<String> tags, Double price, Date date) {
}
