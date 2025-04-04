package gr.bookapp.protocol;

import gr.bookapp.models.Book;
import gr.bookapp.models.Offer;

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
    record GetBooksResponse(List<Book> books) implements Response{
        public static final byte TYPE = 20;
    }
    record GetOffersResponse(List<Offer> offers) implements Response{
        public static final byte TYPE = 21;
    }
}
