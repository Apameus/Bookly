package gr.bookapp.models;

import java.time.Instant;
import java.util.List;

public record Offer(long offerID, List<String> tags, int percentage, Instant untilDate) {
}
