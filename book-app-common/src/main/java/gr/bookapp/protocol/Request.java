package gr.bookapp.protocol;

import gr.bookapp.models.Book;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public interface Request {

    record AuthenticateRequest(String username, String password) implements Request{
        public static final int TYPE = 11;
    }
    record HireEmployeeRequest(String username, String password) implements Request {
        public static final int TYPE = 12;
    }
    record FireEmployeeRequest(long employeeID) implements Request{
        public static final int TYPE = 13;
    }
    record AddBookRequest(Book book) implements Request {
        public static final int TYPE = 20;
    }
    //
    record GetAllBooksRequest() implements Request {
        public static final int TYPE = 21;
    }
    record GetBooksByName(String name) implements Request{
        public static final int TYPE = 22;
    }
    record GetBooksByAuthors(List<String> authors) implements Request{
        public static final int TYPE = 23;
    }
    record GetBooksByTags(List<String> tags) implements Request{
        public static final int TYPE = 24;
    }
    record GetBooksInPriceRange(double min, double max) implements Request{
        public static final int TYPE = 25;
    }
    record GetBooksInDateRange(Instant min, Instant max) implements Request{
        public static final int TYPE = 26;
    }
    //
    record IncreaseSalesOfBook(long bookID) implements Request {
        public static final int TYPE = 27;
    }
    record CreateOfferRequest(List<String> tags, int percentage, Duration duration) implements Request{
        public static final int TYPE = 28;
    }
    record GetOffersByTags(List<String> tags) implements Request{
        public static final int TYPE = 29;
    }
}
