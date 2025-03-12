package gr.bookapp.exceptions;

public final class InvalidInputException extends Throwable {
    public InvalidInputException(String percentage) {
        super("The %s must be greater than 0".formatted(percentage)); //TODO pass the entire msg
    }
}
