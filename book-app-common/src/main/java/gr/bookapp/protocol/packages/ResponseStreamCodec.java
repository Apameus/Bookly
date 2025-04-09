package gr.bookapp.protocol.packages;

import gr.bookapp.models.Book;
import gr.bookapp.models.BookSales;
import gr.bookapp.models.Offer;
import gr.bookapp.models.User;
import gr.bookapp.protocol.codec.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import static gr.bookapp.protocol.packages.Response.*;

public final class ResponseStreamCodec {
    private final UserCodec userCodec;
    private final BookCodec bookCodec;
    private final BookSalesCodec bookSalesCodec;
    private final OfferCodec offerCodec;
    private final StringCodec stringCodec;
    private final ListCodec<Book> listBookCodec;
    private final ListCodec<Offer> listOfferCodec;
    private final ListCodec<User> listUserCodec;

    public ResponseStreamCodec() {
        stringCodec = new StringCodec();
        ListCodec<String> listCodec = new ListCodec<>(stringCodec);
        InstantCodec instantCodec = new InstantCodec();
        userCodec = new UserCodec(stringCodec);
        bookCodec = new BookCodec(stringCodec, listCodec, instantCodec);
        bookSalesCodec = new BookSalesCodec();
        offerCodec = new OfferCodec(listCodec, instantCodec);
        listBookCodec = new ListCodec<Book>(bookCodec);
        listOfferCodec = new ListCodec<Offer>(offerCodec);
        listUserCodec = new ListCodec<>(userCodec);
    }

    public void serialize(DataOutput dataOutput, Response response) throws IOException {
        switch (response){
            case AuthenticateResponse() -> { dataOutput.writeByte(AuthenticateResponse.TYPE); }
            case GeneralSuccessResponse() -> { dataOutput.writeByte(GeneralSuccessResponse.TYPE); }
            case ErrorResponse(String error) -> {
                dataOutput.writeByte(ErrorResponse.TYPE);
                stringCodec.write(dataOutput, error);
            }
            case GetUserResponse(User user) -> {
                dataOutput.write(GetUserResponse.TYPE);
                userCodec.write(dataOutput, user);
            }
            case GetUsersResponse(List<User> users) -> {
                dataOutput.write(GetUsersResponse.TYPE);
                listUserCodec.write(dataOutput, users);
            }
            case GetBookResponse(Book book) -> {
                dataOutput.write(GetBookResponse.TYPE);
                bookCodec.write(dataOutput, book);
            }
            case GetBooksResponse(List<Book> books) -> {
                dataOutput.writeByte(GetBooksResponse.TYPE);
                listBookCodec.write(dataOutput, books);
            }
            case GetOfferResponse(Offer offer) -> {
                dataOutput.write(GetOfferResponse.TYPE);
                offerCodec.write(dataOutput, offer);
            }
            case GetOffersResponse(List<Offer> offers) -> {
                dataOutput.writeByte(GetOffersResponse.TYPE);
                listOfferCodec.write(dataOutput, offers);
            }
            case GetBookSalesResponse(BookSales bookSales) -> {
                dataOutput.write(GetBookSalesResponse.TYPE);
                bookSalesCodec.write(dataOutput, bookSales);
            }
        }
    }

    public Response parse(DataInput dataInput) throws IOException {
        byte type = dataInput.readByte();
        return switch (type){
            case AuthenticateResponse.TYPE -> new AuthenticateResponse();
            case GeneralSuccessResponse.TYPE -> new GeneralSuccessResponse();
            case ErrorResponse.TYPE -> new ErrorResponse(stringCodec.read(dataInput));
            case GetUserResponse.TYPE -> new GetUserResponse(userCodec.read(dataInput));
            case GetUsersResponse.TYPE -> new GetUsersResponse(listUserCodec.read(dataInput));
            case GetBookResponse.TYPE -> new GetBookResponse(bookCodec.read(dataInput));
            case GetBooksResponse.TYPE -> {
                List<Book> books = listBookCodec.read(dataInput);
                yield new GetBooksResponse(books);
            }
            case GetOfferResponse.TYPE -> {
                Offer offer = offerCodec.read(dataInput);
                yield new GetOfferResponse(offer);
            }
            case GetOffersResponse.TYPE -> {
                List<Offer> offers = listOfferCodec.read(dataInput);
                yield new GetOffersResponse(offers);
            }
            case GetBookSalesResponse.TYPE -> {
                BookSales bookSales = bookSalesCodec.read(dataInput);
                yield new GetBookSalesResponse(bookSales);
            }
            default -> throw new IllegalStateException("Unknown Response");
        };
    }
}
