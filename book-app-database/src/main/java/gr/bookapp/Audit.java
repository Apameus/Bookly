package gr.bookapp;

import java.time.Instant;

public record Audit(long employeeID, String action, Instant time) {
}
