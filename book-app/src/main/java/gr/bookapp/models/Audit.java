package gr.bookapp.models;

public record Audit(long employeeID, String action, long time) {
}
