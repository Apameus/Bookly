package gr.bookapp.protocol;

import gr.bookapp.models.Book;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public sealed interface Request {

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
    record GetBooksByNameRequest(String name) implements Request{
        public static final int TYPE = 22;
    }
    record GetBooksByAuthorsRequest(List<String> authors) implements Request{
        public static final int TYPE = 23;
    }
    record GetBooksByTagsRequest(List<String> tags) implements Request{
        public static final int TYPE = 24;
    }
    record GetBooksInPriceRangeRequest(double min, double max) implements Request{
        public static final int TYPE = 25;
    }
    record GetBooksInDateRangeRequest(Instant min, Instant max) implements Request{
        public static final int TYPE = 26;
    }
    //
    record IncreaseSalesOfBookRequest(long bookID) implements Request {
        public static final int TYPE = 27;
    }
    record CreateOfferRequest(List<String> tags, int percentage, Duration duration) implements Request{
        public static final int TYPE = 28;
    }
    record GetOffersByTagsRequest(List<String> tags) implements Request{
        public static final int TYPE = 29;
    }
}
