package gr.bookapp.protocol;

import gr.bookapp.models.Book;
import gr.bookapp.protocol.codec.BookCodec;
import gr.bookapp.protocol.codec.InstantCodec;
import gr.bookapp.protocol.codec.ListCodec;
import gr.bookapp.protocol.codec.StringCodec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static gr.bookapp.protocol.Request.*;

public final class RequestStreamCodec {
    private final BookCodec bookCodec;
    private final StringCodec stringCodec;
    private final ListCodec<String> listCodec;
    private final InstantCodec instantCodec;

    public RequestStreamCodec(BookCodec bookCodec, StringCodec stringCodec, ListCodec<String> listCodec, InstantCodec instantCodec) {
        this.bookCodec = bookCodec;
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
            case HireEmployeeRequest(String username, String password) -> {
                outputStream.write(HireEmployeeRequest.TYPE);
                stringCodec.write(outputStream, username);
                stringCodec.write(outputStream, password);
            }
            case FireEmployeeRequest(long employeeID) -> {
                outputStream.write(FireEmployeeRequest.TYPE);
                outputStream.writeLong(employeeID);
            }
            case AddBookRequest(Book book) -> {
                outputStream.write(AddBookRequest.TYPE);
                bookCodec.write(outputStream, book);
            }
            case GetAllBooksRequest() -> {
                outputStream.write(GetAllBooksRequest.TYPE);
            }
            case GetBooksByNameRequest(String name) -> {
                outputStream.write(GetAllBooksRequest.TYPE);
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
            case IncreaseSalesOfBookRequest(long bookID) -> {
                outputStream.write(IncreaseSalesOfBookRequest.TYPE);
                outputStream.writeLong(bookID);
            }
            case CreateOfferRequest(List<String> tags, int percentage, Duration duration) -> {
                outputStream.write(CreateOfferRequest.TYPE);
                listCodec.write(outputStream, tags);
                outputStream.writeInt(percentage);
                outputStream.writeLong(duration.toDays());
            }
            case GetOffersByTagsRequest(List<String> tags) -> {
                outputStream.write(GetOffersByTagsRequest.TYPE);
                listCodec.write(outputStream, tags);
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
                yield new HireEmployeeRequest(username, password);
            }
            case FireEmployeeRequest.TYPE -> {
                long employeeID = dataInput.readLong();
                yield new FireEmployeeRequest(employeeID);
            }
            case AddBookRequest.TYPE -> {
                Book book = bookCodec.read(dataInput);
                yield new AddBookRequest(book);
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
            case IncreaseSalesOfBookRequest.TYPE -> {
                long bookID = dataInput.readLong();
                yield new IncreaseSalesOfBookRequest(bookID);
            }
            case CreateOfferRequest.TYPE-> {
                List<String> tags = listCodec.read(dataInput);
                int percentage = dataInput.readInt();
                Duration duration = Duration.ofDays(dataInput.readInt());
                yield new CreateOfferRequest(tags, percentage, duration);
            }
            case GetOffersByTagsRequest.TYPE -> {
                List<String> tags = listCodec.read(dataInput);
                yield new GetOffersByTagsRequest(tags);
            }
            default -> throw new IllegalStateException("Unknown Request");
        };
    }
}
