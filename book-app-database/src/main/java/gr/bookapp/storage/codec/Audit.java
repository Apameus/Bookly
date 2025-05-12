package gr.bookapp.storage.codec;

import java.time.Instant;

public record Audit(long userID, String action, Instant time) {
}
