package gr.bookapp.models;

import java.util.List;

public record Sale(List<String> tags, int percentage, long untilDate) { // Should we add saleID ?
}
