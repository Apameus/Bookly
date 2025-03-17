package gr.bookapp.models;

import java.time.Instant;

public record Audit(long employeeID, String action, Instant time) {
}
