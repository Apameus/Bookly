package gr.bookapp.protocol.packages;

import gr.bookapp.models.BookSales;

import java.time.Instant;
import java.util.List;

public sealed interface Request {

    sealed interface User extends Request{ // CHANGE TYPE TO BYTE
        record AuthenticateRequest(String username, String password) implements Request.User{
            public static final byte TYPE = 11;
        }
        record HireEmployeeRequest(String username, String password) implements Request.User {
            public static final byte TYPE = 12;
        }
        record FireEmployeeRequest(long employeeID) implements Request.User {
            public static final byte TYPE = 13;
        }
        record GetUserByIdRequest(long userID) implements Request.User {
            public static final byte TYPE = 14;
        }
        record GetUserByUsernameRequest(String username) implements Request.User {
            public static final byte TYPE = 15;
        }
        record GetAllUsersRequest() implements Request.User {
            public static final byte TYPE = 16;
        }
    }

    sealed interface Book extends Request{
        record AddBookRequest(gr.bookapp.models.Book book) implements Request.Book {
            public static final byte TYPE = 20;
        }
        record DeleteBookRequest(long bookID) implements Request.Book {
            public static final byte TYPE = 21;
        }
        record GetAllBooksRequest() implements Request.Book {
            public static final byte TYPE = 22;
        }
        record GetBooksByNameRequest(String name) implements Request.Book{
            public static final byte TYPE = 23;
        }
        record GetBooksByAuthorsRequest(List<String> authors) implements Request.Book{
            public static final byte TYPE = 24;
        }
        record GetBooksByTagsRequest(List<String> tags) implements Request.Book{
            public static final byte TYPE = 25;
        }
        record GetBooksInPriceRangeRequest(double min, double max) implements Request.Book{
            public static final byte TYPE = 26;
        }
        record GetBooksInDateRangeRequest(Instant min, Instant max) implements Request.Book{
            public static final byte TYPE = 27;
        }
        record GetBookByIdRequest(long bookID) implements Request.Book {
            public static final byte TYPE = 28;
        }
    }

    sealed interface BookSales extends Request{
        record OverrideBookSalesRequest(gr.bookapp.models.BookSales bookSales) implements Request.BookSales{
            public static final byte TYPE = 39;
        }
        record IncreaseSalesOfBookRequest(long bookID, int quantity) implements Request.BookSales {
            public static final byte TYPE = 30;
        }
        record GetBookSalesRequest(long bookID) implements Request.BookSales {
            public static final byte TYPE = 31;
        }
    }

    sealed interface Offer extends Request {
        record CreateOfferRequest(gr.bookapp.models.Offer offer) implements Request.Offer {
            public static final byte TYPE = 32;
        }
        record DeleteOfferRequest(long offerID) implements Request.Offer {
            public static final byte TYPE = 33;
        }
        record GetAllOffersRequest() implements Request.Offer {
            public static final byte TYPE = 34;
        }
        record GetOffersByTagsRequest(List<String> tags) implements Request.Offer {
            public static final byte TYPE = 35;
        }
        record GetOfferByIdRequest(long offerID) implements Request.Offer {
            public static final byte TYPE = 36;
        }
    }
}
