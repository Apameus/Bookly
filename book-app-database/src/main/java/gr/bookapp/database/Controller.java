package gr.bookapp.database;

import gr.bookapp.exceptions.AuthenticationFailedException;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.Book;
import gr.bookapp.models.BookSales;
import gr.bookapp.models.Offer;
import gr.bookapp.models.Role;
import gr.bookapp.models.User;
import gr.bookapp.protocol.packages.Request;
import gr.bookapp.protocol.packages.Response;
import gr.bookapp.services.BookSalesServiceDbImpl;
import gr.bookapp.services.BookServiceDbImpl;
import gr.bookapp.services.OfferServiceDbImpl;
import gr.bookapp.services.UserServiceDbImpl;

import java.time.Instant;
import java.util.List;

import static gr.bookapp.protocol.packages.Request.Book.*;
import static gr.bookapp.protocol.packages.Request.BookSales.*;
import static gr.bookapp.protocol.packages.Request.Offer.*;
import static gr.bookapp.protocol.packages.Request.User.*;
import static gr.bookapp.protocol.packages.Response.*;

public final class Controller {
    private final UserServiceDbImpl userServiceDb;
    private final BookServiceDbImpl bookServiceDb;
    private final BookSalesServiceDbImpl bookSalesServiceDb;
    private final OfferServiceDbImpl offerServiceDb;

    public Controller(UserServiceDbImpl userServiceDb, BookServiceDbImpl bookServiceDb,  BookSalesServiceDbImpl bookSalesServiceDb, OfferServiceDbImpl offerServiceDb) {
        this.userServiceDb = userServiceDb;
        this.bookServiceDb = bookServiceDb;
        this.bookSalesServiceDb = bookSalesServiceDb;
        this.offerServiceDb = offerServiceDb;
    }

    public Response handleRequest(Request request) {
        try {
            return switch (request) {

                // User
                case AuthenticateRequest(String username, String password) -> {
                    userServiceDb.authenticate(username, password);
                    yield new AuthenticateResponse();
                }
                case HireEmployeeRequest(String username, String password) -> {
                    userServiceDb.addUser(username, password, Role.EMPLOYEE);
                    yield new GeneralSuccessResponse();
                }
                case FireEmployeeRequest(long employeeID) -> {
                    userServiceDb.deleteUser(employeeID);
                    yield new GeneralSuccessResponse();
                }
                case GetUserByIdRequest(long userID) -> {
                    User user = userServiceDb.getUserByID(userID);
                    yield new GetUserResponse(user);
                }
                case GetUserByUsernameRequest(String username) -> {
                    User user = userServiceDb.getUserByUsername(username);
                    yield new GetUserResponse(user);
                }
                case GetAllUsersRequest() -> {
                    List<User> users = userServiceDb.getAllUsers();
                    yield new GetUsersResponse(users);
                }

                // Book
                case AddBookRequest(Book book) -> {
                    bookServiceDb.addBook(book);
                    yield new GeneralSuccessResponse();
                }
                case DeleteBookRequest(long bookID) -> {
                    bookServiceDb.deleteBook(bookID);
                    yield new GeneralSuccessResponse();
                }
                case GetAllBooksRequest() -> {
                    List<Book> books = bookServiceDb.getAllBooks();
                    yield new GetBooksResponse(books);
                }
                case GetBookByIdRequest(long bookID) -> {
                    Book book = bookServiceDb.getBookById(bookID);
                    yield new GetBookResponse(book);
                }
                case GetBooksByNameRequest(String name) -> {
                    List<Book> books = bookServiceDb.getBooksByName(name);
                    yield new GetBooksResponse(books);
                }
                case GetBooksByAuthorsRequest(List<String> authors) -> {
                    List<Book> books = bookServiceDb.getBooksByAuthors(authors);
                    yield new GetBooksResponse(books);
                }
                case GetBooksByTagsRequest(List<String> tags) -> {
                    List<Book> books = bookServiceDb.getBooksByTags(tags);
                    yield new GetBooksResponse(books);
                }
                case GetBooksInPriceRangeRequest(double min, double max) -> {
                    List<Book> books = bookServiceDb.getBooksInPriceRange(min, max);
                    yield new GetBooksResponse(books);
                }
                case GetBooksInDateRangeRequest(Instant from, Instant to) -> {
                    List<Book> books = bookServiceDb.getBooksInDateRange(from, to);
                    yield new GetBooksResponse(books);
                }

                // BookSales
                case OverrideBookSalesRequest(BookSales bookSales) -> {
                    bookSalesServiceDb.overrideBookSales(bookSales);
                    yield new GeneralSuccessResponse();
                }
                case IncreaseSalesOfBookRequest(long bookID, int quantity) -> {
                    bookSalesServiceDb.increaseSalesOfBook(bookID, quantity);
                    yield new GeneralSuccessResponse();
                }
                case GetBookSalesRequest(long bookID) -> {
                    BookSales bookSales = bookSalesServiceDb.getBookSales(bookID);
                    yield new GetBookSalesResponse(bookSales);
                }

                // Offer
                case CreateOfferRequest(Offer offer) -> {
                    offerServiceDb.createOffer(offer);
                    yield new GeneralSuccessResponse();
                }
                case DeleteOfferRequest(long offerID) -> {
                    offerServiceDb.deleteOffer(offerID);
                    yield new GeneralSuccessResponse();
                }
                case GetOfferByIdRequest(long offerID) -> {
                    Offer offer = offerServiceDb.getOfferById(offerID);
                    yield new GetOfferResponse(offer);
                }
                case GetAllOffersRequest() -> {
                    List<Offer> offers = offerServiceDb.getAllOffers();
                    yield new GetOffersResponse(offers);
                }
                case GetOffersByTagsRequest(List<String> tags) -> {
                    List<Offer> offers = offerServiceDb.getOffersByTags(tags);
                    yield new GetOffersResponse(offers);
                }
            };
        }
        catch (AuthenticationFailedException e) { return new ErrorResponse("Authentication failed!"); }
        catch (InvalidInputException e) { return new ErrorResponse(e.getMessage()); } //ToDo: Refactor Exception Logic
    }
}
