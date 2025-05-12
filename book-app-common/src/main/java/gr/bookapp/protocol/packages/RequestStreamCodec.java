package gr.bookapp.protocol.packages;

import gr.bookapp.models.Book;
import gr.bookapp.models.BookSales;
import gr.bookapp.models.Offer;
import gr.bookapp.models.Role;
import gr.bookapp.protocol.codec.*;
import gr.bookapp.protocol.packages.Request.Book.GetBooksByAuthorsRequest;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import static gr.bookapp.protocol.packages.Request.Book.*;
import static gr.bookapp.protocol.packages.Request.BookSales.*;
import static gr.bookapp.protocol.packages.Request.Offer.*;
import static gr.bookapp.protocol.packages.Request.User.*;

public final class RequestStreamCodec {
    private final BookCodec bookCodec;
    private final BookSalesCodec bookSalesCodec;
    private final OfferCodec offerCodec;
    private final StringCodec stringCodec;
    private final ListCodec<String> listCodec;
    private final InstantCodec instantCodec;

    public RequestStreamCodec(BookCodec bookCodec, BookSalesCodec bookSalesCodec, OfferCodec offerCodec, StringCodec stringCodec, ListCodec<String> listCodec, InstantCodec instantCodec) {
        this.bookCodec = bookCodec;
        this.bookSalesCodec = bookSalesCodec;
        this.offerCodec = offerCodec;
        this.stringCodec = stringCodec;
        this.listCodec = listCodec;
        this.instantCodec = instantCodec;
    }

    public void serialize(DataOutput outputStream, Request request) throws IOException {
        switch (request) {
            case AuthenticateRequest(String username, String password) -> {
                outputStream.write(AuthenticateRequest.TYPE);
                stringCodec.write(outputStream, username);
                stringCodec.write(outputStream, password);
            }
            case HireEmployeeRequest(String username, String password, String role) -> {
                outputStream.write(HireEmployeeRequest.TYPE);
                stringCodec.write(outputStream, username);
                stringCodec.write(outputStream, password);
                stringCodec.write(outputStream, role);
            }
            case FireEmployeeRequest(long employeeID) -> {
                outputStream.write(FireEmployeeRequest.TYPE);
                outputStream.writeLong(employeeID);
            }
            case GetUserByIdRequest(long userID) -> {
                outputStream.write(GetUserByIdRequest.TYPE);
                outputStream.writeLong(userID);
            }
            case GetUserByUsernameRequest(String username) -> {
                outputStream.write(GetUserByUsernameRequest.TYPE);
                stringCodec.write(outputStream, username);
            }
            case GetAllUsersRequest() -> {
                outputStream.write(GetAllUsersRequest.TYPE);
            }
            case AdminExistRequest() -> {
                outputStream.write(AdminExistRequest.TYPE);
            }
            //
            case AddBookRequest(Book book) -> {
                outputStream.write(AddBookRequest.TYPE);
                bookCodec.write(outputStream, book);
            }
            case DeleteBookRequest(long bookID) -> {
                outputStream.write(DeleteBookRequest.TYPE);
                outputStream.writeLong(bookID);
            }
            case GetAllBooksRequest() -> {
                outputStream.write(GetAllBooksRequest.TYPE);
            }
            case GetBooksByNameRequest(String name) -> {
                outputStream.write(GetBooksByNameRequest.TYPE);
                stringCodec.write(outputStream, name);
            }
            case GetBooksByAuthorsRequest(List<String> authors) -> {
                outputStream.write(GetBooksByAuthorsRequest.TYPE);
                listCodec.write(outputStream, authors);
            }
            case GetBooksByTagsRequest(List<String> tags) -> {
                outputStream.write(GetBooksByTagsRequest.TYPE);
                listCodec.write(outputStream, tags);
            }
            case GetBooksInPriceRangeRequest(double min, double max) -> {
                outputStream.write(GetBooksInPriceRangeRequest.TYPE);
                outputStream.writeDouble(min);
                outputStream.writeDouble(max);
            }
            case GetBooksInDateRangeRequest(Instant min, Instant max) -> {
                outputStream.write(GetBooksInDateRangeRequest.TYPE);
                instantCodec.write(outputStream, min);
                instantCodec.write(outputStream, max);
            }
            case GetBookByIdRequest(long bookID) -> {
                outputStream.write(GetBookByIdRequest.TYPE);
                outputStream.writeLong(bookID);
            }
            case OverrideBookSalesRequest(BookSales bookSales) -> {
                outputStream.write(OverrideBookSalesRequest.TYPE);
                bookSalesCodec.write(outputStream, bookSales);
            }
            case IncreaseSalesOfBookRequest(long bookID, int quantity) -> {
                outputStream.write(IncreaseSalesOfBookRequest.TYPE);
                outputStream.writeLong(bookID);
            }
            case GetBookSalesRequest(long bookID) -> {
                outputStream.write(GetBookSalesRequest.TYPE);
                outputStream.writeLong(bookID);
            }
            case CreateOfferRequest(Offer offer) -> {
                outputStream.write(CreateOfferRequest.TYPE);
                offerCodec.write(outputStream, offer);
            }
            case DeleteOfferRequest(long offerID) -> {
                outputStream.write(DeleteOfferRequest.TYPE);
                outputStream.writeLong(offerID);
            }
            case GetAllOffersRequest() -> {
                outputStream.write(GetAllOffersRequest.TYPE);
            }
            case GetOffersByTagsRequest(List<String> tags) -> {
                outputStream.write(GetOffersByTagsRequest.TYPE);
                listCodec.write(outputStream, tags);
            }
            case GetOfferByIdRequest(long offerID) -> {
                outputStream.write(GetOfferByIdRequest.TYPE);
                outputStream.writeLong(offerID);
            }

        }
    }

    public Request parse(DataInput dataInput) throws IOException {
        byte type = dataInput.readByte();
        return switch (type) {
            case AuthenticateRequest.TYPE -> {
                String username = stringCodec.read(dataInput);
                String password = stringCodec.read(dataInput);
                yield new AuthenticateRequest(username, password);
            }
            case HireEmployeeRequest.TYPE -> {
                String username = stringCodec.read(dataInput);
                String password = stringCodec.read(dataInput);
                String role = stringCodec.read(dataInput);
                yield new HireEmployeeRequest(username, password, role);
            }
            case FireEmployeeRequest.TYPE -> {
                long employeeID = dataInput.readLong();
                yield new FireEmployeeRequest(employeeID);
            }
            case GetUserByIdRequest.TYPE -> {
                long userID = dataInput.readLong();
                yield new GetUserByIdRequest(userID);
            }
            case GetUserByUsernameRequest.TYPE -> {
                String username = stringCodec.read(dataInput);
                yield new GetUserByUsernameRequest(username);
            }
            case GetAllUsersRequest.TYPE -> {
                yield new GetAllUsersRequest();
            }
            case AdminExistRequest.TYPE -> {
                yield new AdminExistRequest();
            }
            case AddBookRequest.TYPE -> {
                Book book = bookCodec.read(dataInput);
                yield new AddBookRequest(book);
            }
            case DeleteBookRequest.TYPE -> {
                long bookID = dataInput.readLong();
                yield new DeleteBookRequest(bookID);
            }
            case GetAllBooksRequest.TYPE -> {
                yield new GetAllBooksRequest();
            }
            case GetBooksByNameRequest.TYPE -> {
                String name = stringCodec.read(dataInput);
                yield new GetBooksByNameRequest(name);
            }
            case GetBooksByAuthorsRequest.TYPE -> {
                List<String> authors = listCodec.read(dataInput);
                yield new GetBooksByAuthorsRequest(authors);
            }
            case GetBooksByTagsRequest.TYPE -> {
                List<String> tags = listCodec.read(dataInput);
                yield new GetBooksByTagsRequest(tags);
            }
            case GetBooksInPriceRangeRequest.TYPE -> {
                double min = dataInput.readDouble();
                double max = dataInput.readDouble();
                yield new GetBooksInPriceRangeRequest(min, max);
            }
            case GetBooksInDateRangeRequest.TYPE -> {
                Instant min = instantCodec.read(dataInput);
                Instant max = instantCodec.read(dataInput);
                yield new GetBooksInDateRangeRequest(min, max);
            }
            case GetBookByIdRequest.TYPE -> {
                long bookID = dataInput.readLong();
                yield new GetBookByIdRequest(bookID);
            }
            case OverrideBookSalesRequest.TYPE -> {
                BookSales bookSales = bookSalesCodec.read(dataInput);
                yield new OverrideBookSalesRequest(bookSales);
            }
            case IncreaseSalesOfBookRequest.TYPE -> {
                long bookID = dataInput.readLong();
                int quantity = dataInput.readInt();
                yield new IncreaseSalesOfBookRequest(bookID, quantity);
            }
            case GetBookSalesRequest.TYPE -> {
                long bookID = dataInput.readLong();
                yield new GetBookSalesRequest(bookID);
            }
            case CreateOfferRequest.TYPE-> {
                Offer offer = offerCodec.read(dataInput);
                yield new CreateOfferRequest(offer);
            }
            case DeleteOfferRequest.TYPE -> {
                long offerID = dataInput.readLong();
                yield new DeleteOfferRequest(offerID);
            }
            case GetAllOffersRequest.TYPE -> {
                yield new GetAllOffersRequest();
            }
            case GetOffersByTagsRequest.TYPE -> {
                List<String> tags = listCodec.read(dataInput);
                yield new GetOffersByTagsRequest(tags);
            }
            case GetOfferByIdRequest.TYPE -> {
                long offerID = dataInput.readLong();
                yield new GetOfferByIdRequest(offerID);
            }
            default -> throw new IllegalStateException("Unknown Request");
        };
    }
}
