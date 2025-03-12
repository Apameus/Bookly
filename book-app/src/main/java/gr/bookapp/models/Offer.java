package gr.bookapp.models;

import java.util.List;

public record Offer(long offerID, List<String> tags, int percentage, long untilDate) {
}
