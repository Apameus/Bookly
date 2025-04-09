package gr.bookapp.protocol.packages;

import gr.bookapp.models.Book;
import gr.bookapp.models.BookSales;
import gr.bookapp.models.Offer;
import gr.bookapp.models.User;

import java.util.List;

public sealed interface Response {
    record AuthenticateResponse() implements Response{
        public static final byte TYPE = 11;
    }
    record GeneralSuccessResponse() implements Response{
        public static final byte TYPE = 80;
    }
    record ErrorResponse(String error) implements Response{
        public static final byte TYPE = 90;
    }
    record GetUserResponse(User user) implements Response{ // Is this worth it or should we use the List response and pass only that single element??
        public static final byte TYPE = 20;
    }
    record GetUsersResponse(List<User> users) implements Response{
        public static final byte TYPE = 21;
    }
    record GetBookResponse(Book book) implements Response{
        public static final byte TYPE = 22;
    }
    record GetBooksResponse(List<Book> books) implements Response{
        public static final byte TYPE = 23;
    }
    record GetOfferResponse(Offer offer) implements Response{ // Is this worth it or should we use the List response and pass only that single element??
        public static final byte TYPE = 24;
    }
    record GetOffersResponse(List<Offer> offers) implements Response{
        public static final byte TYPE = 25;
    }
    record GetBookSalesResponse(BookSales bookSales) implements Response{
        public static final byte TYPE = 26;
    }
}
