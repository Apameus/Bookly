package gr.bookapp.repositories;

import gr.bookapp.client.Client;
import gr.bookapp.models.Book;
import gr.bookapp.protocol.packages.Request;
import gr.bookapp.protocol.packages.Response;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static gr.bookapp.protocol.packages.Request.Book.*;

public final class BookRepositoryClientImpl implements BookRepository{
    private final Client client;

    public BookRepositoryClientImpl(Client client) {
        this.client = client;
    }

    @Override
    public List<Book> findBooksWithName(String name) {
        Response response = client.send(new GetBooksByNameRequest(name));
        return switch (response) {
            case Response.GetBooksResponse(List<Book> books) -> books;
            case Response.ErrorResponse(String error) -> new ArrayList<>();
            default -> throw new IllegalStateException("Unexpected response: " + response);
        };
    }

    @Override
    public List<Book> findBooksWithAuthors(List<String> authors) {
        Response response = client.send(new GetBooksByAuthorsRequest(authors));
        return switch (response) {
            case Response.GetBooksResponse(List<Book> books) -> books;
            case Response.ErrorResponse(String error) -> new ArrayList<>();
            default -> throw new IllegalStateException("Unexpected response: " + response);
        };
    }

    @Override
    public List<Book> findBooksWithTags(List<String> tags) {
        Response response = client.send(new GetBooksByTagsRequest(tags));
        return switch (response) {
            case Response.GetBooksResponse(List<Book> books) -> books;
            case Response.ErrorResponse(String error) -> new ArrayList<>();
            default -> throw new IllegalStateException("Unexpected response: " + response);
        };
    }

    @Override
    public List<Book> findBooksInPriceRange(double min, double max) {
        Response response = client.send(new GetBooksInPriceRangeRequest(min, max));
        return switch (response) {
            case Response.GetBooksResponse(List<Book> books) -> books;
            case Response.ErrorResponse(String error) -> new ArrayList<>();
            default -> throw new IllegalStateException("Unexpected response: " + response);
        };
    }

    @Override
    public List<Book> findBooksInDateRange(Instant from, Instant to) {
        Response response = client.send(new GetBooksInDateRangeRequest(from, to));
        return switch (response) {
            case Response.GetBooksResponse(List<Book> books) -> books;
            case Response.ErrorResponse(String error) -> new ArrayList<>();
            default -> throw new IllegalStateException("Unexpected response: " + response);
        };
    }

    @Override
    public List<Book> getAllBooks() {
        Response response = client.send(new GetAllBooksRequest());
        return switch (response) {
            case Response.GetBooksResponse(List<Book> books) -> books;
            case Response.ErrorResponse(String error) -> new ArrayList<>();
            default -> throw new IllegalStateException("Unexpected response: " + response);
        };
    }

    @Override
    public void add(Book book) {
        Response response = client.send(new AddBookRequest(book));
        switch (response) {
            case Response.GeneralSuccessResponse() -> {}
            case Response.ErrorResponse(String error) -> {}
            default -> throw new IllegalStateException("Unexpected response: " + response);
        };
    }

    @Override
    public void deleteBookByID(long bookID) {
        Response response = client.send(new DeleteBookRequest(bookID));
        switch (response) {
            case Response.GeneralSuccessResponse() -> {}
            case Response.ErrorResponse(String error) -> {}
            default -> throw new IllegalStateException("Unexpected response: " + response);
        };
    }

    @Override
    public Book getBookByID(long bookID) {
        Response response = client.send(new GetBookByIdRequest(bookID));
        return switch (response) {
            case Response.GetBookResponse(Book book) -> book;
            case Response.ErrorResponse(String error) -> null;
            default -> throw new IllegalStateException("Unexpected response: " + response);
        };
    }
}
