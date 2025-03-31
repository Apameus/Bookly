package gr.bookapp.protocol;

import gr.bookapp.models.Book;
import gr.bookapp.models.Offer;

import java.util.List;

public interface Response {
    record AuthenticateResponse() implements Response{
        public static final int TYPE = 11;
    }
    record GeneralSuccessResponse() implements Response{
        public static final int TYPE = 80;
    }
    record ErrorResponse(int exceptionType) implements Response{
        public static final int TYPE = 90;
    }
    record GetBooksResponse(List<Book> books) implements Response{
        public static final int TYPE = 20;
    }
    record GetOffersResponse(List<Offer> offers) implements Response{
        public static final int TYPE = 21;
    }
}
