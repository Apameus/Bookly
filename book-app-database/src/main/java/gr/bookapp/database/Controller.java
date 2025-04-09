package gr.bookapp.database;

import gr.bookapp.common.IdGenerator;
import gr.bookapp.exceptions.AuthenticationFailedException;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.Book;
import gr.bookapp.models.BookSales;
import gr.bookapp.models.Offer;
import gr.bookapp.models.Role;
import gr.bookapp.models.User;
import gr.bookapp.protocol.packages.Request;
import gr.bookapp.protocol.packages.Response;
import gr.bookapp.repositories.BookRepositoryDbImpl;
import gr.bookapp.repositories.BookSalesRepositoryDbImpl;
import gr.bookapp.repositories.OfferRepositoryDbImpl;
import gr.bookapp.repositories.UserRepositoryDbImpl;
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
    private final BookRepositoryDbImpl bookRepositoryDb;
    private final BookSalesRepositoryDbImpl bookSalesRepositoryDb;
    private final OfferRepositoryDbImpl offerRepositoryDb;
    private final IdGenerator idGenerator;

    public Controller(UserServiceDbImpl userServiceDb, BookRepositoryDbImpl bookRepositoryDb, BookSalesRepositoryDbImpl bookSalesRepositoryDb, OfferRepositoryDbImpl offerRepositoryDb, IdGenerator idGenerator) {
        this.userServiceDb = userServiceDb;
        this.bookRepositoryDb = bookRepositoryDb;
        this.bookSalesRepositoryDb = bookSalesRepositoryDb;
        this.offerRepositoryDb = offerRepositoryDb;
        this.idGenerator = idGenerator;
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
                    bookRepositoryDb.add(book);
                    yield new GeneralSuccessResponse();
                }
                case DeleteBookRequest(long bookID) -> {
                    if (bookRepositoryDb.getBookByID(bookID) == null)
                        yield new ErrorResponse("Book with specified id does NOT exist!");
                    bookRepositoryDb.deleteBookByID(bookID);
                    yield new GeneralSuccessResponse();
                }
                case GetAllBooksRequest() -> {
                    List<Book> books = bookRepositoryDb.getAllBooks();
                    if (books.isEmpty()) yield new ErrorResponse("No book are registered!");
                    yield new GetBooksResponse(books);
                }
                case GetBookByIdRequest(long bookID) -> {
                    Book book = bookRepositoryDb.getBookByID(bookID);
                    if (book == null) yield new ErrorResponse("Book with specified id does NOT exist!");
                    yield new GetBookResponse(book);
                }
                case GetBooksByNameRequest(String name) -> {
                    List<Book> books = bookRepositoryDb.findBooksWithName(name);
                    if (books.isEmpty()) yield new ErrorResponse("No books with specified name registered!");
                    yield new GetBooksResponse(books);
                }
                case GetBooksByAuthorsRequest(List<String> authors) -> {
                    List<Book> books = bookRepositoryDb.findBooksWithAuthors(authors);
                    if (books.isEmpty()) yield new ErrorResponse("No books with specified authors registered!");
                    yield new GetBooksResponse(books);
                }
                case GetBooksByTagsRequest(List<String> tags) -> {
                    List<Book> books = bookRepositoryDb.findBooksWithTags(tags);
                    if (books.isEmpty()) yield new ErrorResponse("No books with specified tags registered!");
                    yield new GetBooksResponse(books);
                }
                case GetBooksInPriceRangeRequest(double min, double max) -> {
                    List<Book> books = bookRepositoryDb.findBooksInPriceRange(min, max);
                    if (books.isEmpty()) yield new ErrorResponse("No books with specified price-range registered!");
                    yield new GetBooksResponse(books);
                }
                case GetBooksInDateRangeRequest(Instant from, Instant to) -> {
                    List<Book> books = bookRepositoryDb.findBooksInDateRange(from, to);
                    if (books.isEmpty()) yield new ErrorResponse("No books with specified authors registered!");
                    yield new GetBooksResponse(books);
                }

                // BookSales
                case OverrideBookSalesRequest(BookSales bookSales) -> {
                    bookSalesRepositoryDb.add(bookSales);
                    yield new GeneralSuccessResponse();
                }
                case IncreaseSalesOfBookRequest(long bookID, int quantity) -> {
                    if (bookSalesRepositoryDb.getBookSalesByBookID(bookID) == null) yield new ErrorResponse("BookSales not found!");
                    if (quantity <= 0) yield new ErrorResponse("Quantity must be greater than 0!");
                    bookSalesRepositoryDb.increaseSalesOfBook(bookID, quantity);
                    yield new GeneralSuccessResponse();
                }
                case GetBookSalesRequest(long bookID) -> {
                    BookSales bookSales = bookSalesRepositoryDb.getBookSalesByBookID(bookID);
                    if (bookSales == null) yield new ErrorResponse("BookSales not found!");
                    yield new GetBookSalesResponse(bookSales);
                }

                // Offer
                case CreateOfferRequest(Offer offer) -> {
                    offerRepositoryDb.add(offer);
                    yield new GeneralSuccessResponse();
                }
                case DeleteOfferRequest(long offerID) -> {
                    if (offerRepositoryDb.getOfferById(offerID) == null) yield new ErrorResponse("Offer with specified id does NOT exist!");
                    offerRepositoryDb.deleteOfferById(offerID);
                    yield new GeneralSuccessResponse();
                }
                case GetOfferByIdRequest(long offerID) -> {
                    Offer offer = offerRepositoryDb.getOfferById(offerID);
                    if (offer == null) yield new ErrorResponse("Offer with specified id does NOT exist!");
                    yield new GetOfferResponse(offer);
                }
                case GetAllOffersRequest() -> {
                    List<Offer> offers = offerRepositoryDb.getAllOffers();
                    if (offers.isEmpty()) yield new ErrorResponse("No offers are registered!");
                    yield new GetOffersResponse(offers);
                }
                case GetOffersByTagsRequest(List<String> tags) -> {
                    List<Offer> offers = offerRepositoryDb.getOffersByTags(tags);
                    if (offers.isEmpty()) yield new ErrorResponse("No offers with specified tags are registered!");
                    yield new GetOffersResponse(offers);
                }
            };
        }
        catch (AuthenticationFailedException e) { return new ErrorResponse("Authentication failed!"); }
        catch (InvalidInputException e) { return new ErrorResponse(""); } //ToDo: Refactor Exception Logic
    }
}
