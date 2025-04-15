package gr.bookapp.database;

import gr.bookapp.common.IdGenerator;
import gr.bookapp.common.InstantFormatter;
import gr.bookapp.models.Book;
import gr.bookapp.models.BookSales;
import gr.bookapp.models.Offer;
import gr.bookapp.models.Role;
import gr.bookapp.models.User;
import gr.bookapp.repositories.*;
import gr.bookapp.services.BookSalesServiceDbImpl;
import gr.bookapp.services.BookServiceDbImpl;
import gr.bookapp.services.OfferServiceDbImpl;
import gr.bookapp.services.UserServiceDbImpl;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import gr.bookapp.protocol.packages.Request.User.AuthenticateRequest;
import static gr.bookapp.protocol.packages.Request.Book.*;
import static gr.bookapp.protocol.packages.Request.BookSales.*;
import static gr.bookapp.protocol.packages.Request.Offer.*;
import static gr.bookapp.protocol.packages.Request.User.*;
import gr.bookapp.protocol.packages.Response;
import static gr.bookapp.protocol.packages.Response.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
class ControllerTest {

//    @Mock
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    UserServiceDbImpl userServiceDb;
//    @Mock
    BookRepository bookRepository = Mockito.mock(BookRepository.class);
    BookServiceDbImpl bookServiceDb;
//    @Mock
    BookSalesRepository bookSalesRepository = Mockito.mock(BookSalesRepository.class);
    BookSalesServiceDbImpl bookSalesServiceDb;
//    @Mock
    OfferRepository offerRepository = Mockito.mock(OfferRepository.class);
    OfferServiceDbImpl offerServiceDb;
//    @InjectMocks
    Controller controller;

    @BeforeEach
    void setup(){
        userServiceDb = new UserServiceDbImpl(userRepository, Mockito.mock(IdGenerator.class));
        bookServiceDb = new BookServiceDbImpl(bookRepository);
        bookSalesServiceDb = new BookSalesServiceDbImpl(bookSalesRepository);
        offerServiceDb = new OfferServiceDbImpl(offerRepository);
        controller = new Controller(userServiceDb, bookServiceDb, bookSalesServiceDb, offerServiceDb);
    }


    // **********************     USER    ************************

    @Test
    @DisplayName("Handle Authenticate Request with valid credentials")
    void handleAuthenticateRequestWithValidCredentials() {
        when(userRepository.getUserByUsername("makis")).thenReturn(new User("makis", "makis123", Role.EMPLOYEE));
//        assertDoesNotThrow(() -> userServiceDb.authenticate("makis", "makis123"));

        AuthenticateRequest authenticateRequest = new AuthenticateRequest("makis", "makis123");
        Response response = controller.handleRequest(authenticateRequest);

        assertThat(response).isEqualTo(new AuthenticateResponse());
    }

    @Test
    @DisplayName("Handle Authenticate Request with invalid credentials")
    void handleAuthenticateRequestWithInvalidCredentials() {
        when(userRepository.getUserByUsername("fail")).thenReturn(null);

        AuthenticateRequest authenticateRequest = new AuthenticateRequest("fail", "fail");
        Response response = controller.handleRequest(authenticateRequest);

        assertThat(response).isEqualTo(new ErrorResponse("Authentication failed!"));
    }

    @Test
    @DisplayName("Handle HireEmployeeRequest")
    void handleHireEmployeeRequest() {
        when(userRepository.getUserByUsername("makis")).thenReturn(null);

        HireEmployeeRequest hireEmployeeRequest = new HireEmployeeRequest("makis", "makis123");
        Response response = controller.handleRequest(hireEmployeeRequest);

        assertThat(response).isEqualTo(new GeneralSuccessResponse());
    }

    @Test
    @DisplayName("Handle HireEmployeeRequest when the same username already exist")
    void handleHireEmployeeRequestWhenTheSameUsernameAlreadyExist() {
        when(userRepository.getUserByUsername("makis")).thenReturn(new User("makis","ffff0900", Role.ADMIN));

        HireEmployeeRequest hireEmployeeRequest = new HireEmployeeRequest("makis", "makis123");
        Response response = controller.handleRequest(hireEmployeeRequest);

        assertThat(response).isEqualTo(new ErrorResponse("Username already exist!"));
    }

    @Test
    @DisplayName("Handle FireEmployeeRequest")
    void handleFireEmployeeRequest() {
        when(userRepository.getUserByID(1)).thenReturn(new User("existingUser", "1234", Role.EMPLOYEE));

        FireEmployeeRequest fireEmployeeRequest = new FireEmployeeRequest(1);
        Response response = controller.handleRequest(fireEmployeeRequest);

        assertThat(response).isEqualTo(new GeneralSuccessResponse());
    }

    @Test
    @DisplayName("Handle FireEmployeeRequest when employeeID is invalid")
    void handleFireEmployeeRequestWhenEmployeeIdIsInvalid() {
        when(userRepository.getUserByID(anyLong())).thenReturn(null);

        FireEmployeeRequest fireEmployeeRequest = new FireEmployeeRequest(1);
        Response response = controller.handleRequest(fireEmployeeRequest);

        assertThat(response).isEqualTo(new ErrorResponse("User with specified id does NOT exist!"));
    }

    @Test
    @DisplayName("Handle GetUserByIdRequest")
    void handleGetUserByIdRequest() {
        User someUser = new User("some_user", "1234", Role.EMPLOYEE);
        when(userRepository.getUserByID(1)).thenReturn(someUser);

        GetUserByIdRequest getUserByIdRequest = new GetUserByIdRequest(1);
        Response response = controller.handleRequest(getUserByIdRequest);

        assertThat(response).isEqualTo(new GetUserResponse(someUser));
    }

    @Test
    @DisplayName("Handle GetUserByIdRequest with invalid id")
    void handleGetUserByIdRequestWithInvalidId() {
        when(userRepository.getUserByID(1)).thenReturn(null);

        GetUserByIdRequest getUserByIdRequest = new GetUserByIdRequest(1);
        Response response = controller.handleRequest(getUserByIdRequest);

        assertThat(response).isEqualTo(new ErrorResponse("User with specified id does NOT exist!"));
    }

    @Test
    @DisplayName("Handle GetUserByUsernameRequest")
    void handleGetUserByUsernameRequest() {
        User someUser = new User("some_user", "1234", Role.EMPLOYEE);
        when(userRepository.getUserByUsername("some_user")).thenReturn(someUser);

        GetUserByUsernameRequest getUserByUsernameRequest = new GetUserByUsernameRequest("some_user");
        Response response = controller.handleRequest(getUserByUsernameRequest);

        assertThat(response).isEqualTo(new GetUserResponse(someUser));
    }

    @Test
    @DisplayName("Handle GetUserByUsernameRequest with invalid username")
    void handleGetUserByUsernameRequestWithInvalidUsername() {
        when(userRepository.getUserByUsername("some_user")).thenReturn(null);

        GetUserByUsernameRequest getUserByUsernameRequest = new GetUserByUsernameRequest("some_user");
        Response response = controller.handleRequest(getUserByUsernameRequest);

        assertThat(response).isEqualTo(new ErrorResponse("User with specified username does NOT exist!"));
    }

    @Test
    @DisplayName("Handle GetAllUsersRequest")
    void handleGetAllUsersRequest() {
        List<User> users = List.of(new User("some_user", "1234", Role.EMPLOYEE), new User("anotherUser", "1234", Role.EMPLOYEE));
        when(userRepository.getAll()).thenReturn(users);

        GetAllUsersRequest getAllUsersRequest = new GetAllUsersRequest();
        Response response = controller.handleRequest(getAllUsersRequest);

        assertThat(response).isEqualTo(new GetUsersResponse(users));
    }

    // **********************     BOOK    ************************

    @Test
    @DisplayName("Handle AddBookRequest")
    void handleAddBookRequest() {
        Book book = new Book(1, "Odyssey", List.of("Omiros"), 100, InstantFormatter.parse("01-02-2000 AD"), List.of("Philosophy", "Advanture"));

        AddBookRequest addBookRequest = new AddBookRequest(book);
        Response response = controller.handleRequest(addBookRequest);

        assertThat(response).isEqualTo(new GeneralSuccessResponse());
    }


    @Test
    @DisplayName("Handle DeleteBookRequest")
    void handleDeleteBookRequest() {
        Book book = new Book(1, "Odyssey", List.of("Omiros"), 100, InstantFormatter.parse("01-02-2000 AD"), List.of("Philosophy", "Advanture"));
        when(bookRepository.getBookByID(1)).thenReturn(book);

        DeleteBookRequest deleteBookRequest = new DeleteBookRequest(1);
        Response response = controller.handleRequest(deleteBookRequest);

        assertThat(response).isEqualTo(new GeneralSuccessResponse());
    }

    @Test
    @DisplayName("Handle DeleteBookRequest with invalid id")
    void handleDeleteBookRequestWithInvalidId() {
        when(bookRepository.getBookByID(1)).thenReturn(null);

        DeleteBookRequest deleteBookRequest = new DeleteBookRequest(1);
        Response response = controller.handleRequest(deleteBookRequest);

        assertThat(response).isEqualTo(new ErrorResponse("Book with specified id does NOT exist!"));
    }

    @Test
    @DisplayName("Handle GetAllBooksRequest")
    void handleGetAllBooksRequest() {
        Book bookA = new Book(1, "Odyssey", List.of("Omiros"), 100, InstantFormatter.parse("01-02-2000 AD"), List.of("Philosophy", "Advanture"));
        Book bookB = new Book(1, "Odyssey", List.of("Omiros"), 100, InstantFormatter.parse("01-02-2000 AD"), List.of("Philosophy", "Advanture"));
        when(bookRepository.getAllBooks()).thenReturn(List.of(bookA, bookB));

        GetAllBooksRequest getAllBooksRequest = new GetAllBooksRequest();
        Response response = controller.handleRequest(getAllBooksRequest);

        assertThat(response).isEqualTo(new GetBooksResponse(List.of(bookA, bookB)));
    }

    @Test
    @DisplayName("Handle GetAllBooksRequest with zero registered books")
    void handleGetAllBooksRequestWithZeroRegisteredBooks() {
        GetAllBooksRequest getAllBooksRequest = new GetAllBooksRequest();
        Response response = controller.handleRequest(getAllBooksRequest);

        assertThat(response).isEqualTo(new ErrorResponse("No books are registered!"));
    }

    @Test
    @DisplayName("Handle GetBookByIdRequest")
    void handleGetBookByIdRequest() {
        Book book = Instancio.create(Book.class);
        when(bookRepository.getBookByID(1)).thenReturn(book);

        GetBookByIdRequest getBookByIdRequest = new GetBookByIdRequest(1);
        Response response = controller.handleRequest(getBookByIdRequest);

        assertThat(response).isEqualTo(new GetBookResponse(book));
    }

    @Test
    @DisplayName("Handle GetBookByIdRequest with invalid id")
    void handleGetBookByIdRequestWithInvalidId() {
        when(bookRepository.getBookByID(1)).thenReturn(null);

        GetBookByIdRequest getBookByIdRequest = new GetBookByIdRequest(1);
        Response response = controller.handleRequest(getBookByIdRequest);

        assertThat(response).isEqualTo(new ErrorResponse("Book with specified id does NOT exist!"));
    }


    @Test
    @DisplayName("Handle GetBooksByNameRequest")
    void handleGetBooksByNameRequest() {
        List<Book> books = Instancio.ofList(Book.class).size(2).set(field(Book::name), "sameName").create();
        when(bookRepository.findBooksWithName("sameName")).thenReturn(books);

        GetBooksByNameRequest getBooksByNameRequest = new GetBooksByNameRequest("sameName");
        Response response = controller.handleRequest(getBooksByNameRequest);

        assertThat(response).isEqualTo(new GetBooksResponse(books));
    }

    @Test
    @DisplayName("Handle GetBooksByNameRequest with non-existing name")
    void handleGetBooksByNameRequestWithNonExistingName() {
        List<Book> books = Instancio.ofList(Book.class).size(2).set(field(Book::name), "sameName").create();
        when(bookRepository.findBooksWithName("sameName")).thenReturn(new ArrayList<>());

        GetBooksByNameRequest getBooksByNameRequest = new GetBooksByNameRequest("sameName");
        Response response = controller.handleRequest(getBooksByNameRequest);

        assertThat(response).isEqualTo(new ErrorResponse("No books with specified name registered!"));
    }

    @Test
    @DisplayName("Handle GetBooksByAuthorsRequest")
    void handleGetBooksByAuthorsRequest() {
        List<String> authors = List.of("Omiros", "Other");
        List<Book> books = Instancio.ofList(Book.class).size(2).set(field(Book::authors), authors).create();
        when(bookRepository.findBooksWithAuthors(authors)).thenReturn(books);

        GetBooksByAuthorsRequest getBooksByAuthorsRequest = new GetBooksByAuthorsRequest(authors);
        Response response = controller.handleRequest(getBooksByAuthorsRequest);

        assertThat(response).isEqualTo(new GetBooksResponse(books));
    }

    @Test
    @DisplayName("Handle GetBooksByAuthorsRequest with non-existing authors")
    void handleGetBooksByAuthorsRequestWithNonExistingAuthors() {
        List<String> authors = List.of("Omiros", "Other");
        when(bookRepository.findBooksWithAuthors(authors)).thenReturn(new ArrayList<>());

        GetBooksByAuthorsRequest getBooksByAuthorsRequest = new GetBooksByAuthorsRequest(authors);
        Response response = controller.handleRequest(getBooksByAuthorsRequest);

        assertThat(response).isEqualTo(new ErrorResponse("No books with specified authors registered!"));
    }

    @Test
    @DisplayName("Handle GetBooksByTagsRequest")
    void handleGetBooksByTagsRequest() {
        List<String> tags = List.of("Philosophy", "Adventure");
        List<Book> books = Instancio.ofList(Book.class).size(2).set(field(Book::tags), tags).create();
        when(bookRepository.findBooksWithTags(tags)).thenReturn(books);

        GetBooksByTagsRequest getBooksByTagsRequest = new GetBooksByTagsRequest(tags);
        Response response = controller.handleRequest(getBooksByTagsRequest);

        assertThat(response).isEqualTo(new GetBooksResponse(books));
    }

    @Test
    @DisplayName("Handle GetBooksByTagsRequest with non-existing tags")
    void handleGetBooksByTagsRequestWithNonExistingTags() {
        List<String> tags = List.of("Philosophy", "Adventure");
        when(bookRepository.findBooksWithTags(tags)).thenReturn(new ArrayList<>());

        GetBooksByTagsRequest getBooksByTagsRequest = new GetBooksByTagsRequest(tags);
        Response response = controller.handleRequest(getBooksByTagsRequest);

        assertThat(response).isEqualTo(new ErrorResponse("No books with specified tags registered!"));
    }

    @Test
    @DisplayName("Handle GetBooksInPriceRangeRequest")
    void handleGetBooksInPriceRangeRequest() {
        List<Book> books = Instancio.ofList(Book.class).size(2).set(field(Book::price), 50).create();
        when(bookRepository.findBooksInPriceRange(40, 100)).thenReturn(books);

        GetBooksInPriceRangeRequest getBooksInPriceRangeRequest = new GetBooksInPriceRangeRequest(40, 100);
        Response response = controller.handleRequest(getBooksInPriceRangeRequest);

        assertThat(response).isEqualTo(new GetBooksResponse(books));
    }

    @Test
    @DisplayName("Handle GetBooksInPriceRangeRequest with no books in this price range")
    void handleGetBooksInPriceRangeRequestWithNoBooksInThisPriceRange() {
        when(bookRepository.findBooksInPriceRange(40,70)).thenReturn(new ArrayList<>());

        GetBooksInPriceRangeRequest getBooksInPriceRangeRequest = new GetBooksInPriceRangeRequest(40,70);
        Response response = controller.handleRequest(getBooksInPriceRangeRequest);

        assertThat(response).isEqualTo(new ErrorResponse("No books in specified price-range registered!"));
    }

    @Test
    @DisplayName("Handle GetBooksInDateRangeRequest")
    void handleGetBooksInDateRangeRequest() {
        Instant releaseDate = InstantFormatter.parse("01-02-2020 AD");
        List<Book> books = Instancio.ofList(Book.class).size(2).set(field(Book::releaseDate), releaseDate).create();

        Instant minDate = InstantFormatter.parse("20-08-2019 AD");
        Instant maxDate = InstantFormatter.parse("27-04-2025 AD");
        when(bookRepository.findBooksInDateRange( minDate, maxDate )).thenReturn(books);
        GetBooksInDateRangeRequest getBooksInDateRangeRequest = new GetBooksInDateRangeRequest(minDate, maxDate);
        Response response = controller.handleRequest(getBooksInDateRangeRequest);

        assertThat(response).isEqualTo(new GetBooksResponse(books));
    }

    @Test
    @DisplayName("Handle GetBooksInDateRangeRequest with no books in this date range")
    void handleGetBooksInDateRangeRequestWithNoBooksInThisDateRange() {
        Instant minDate = InstantFormatter.parse("20-08-2019 AD");
        Instant maxDate = InstantFormatter.parse("27-04-2025 AD");
        when(bookRepository.findBooksInDateRange( minDate, maxDate )).thenReturn(new ArrayList<>());
        GetBooksInDateRangeRequest getBooksInDateRangeRequest = new GetBooksInDateRangeRequest(minDate, maxDate);
        Response response = controller.handleRequest(getBooksInDateRangeRequest);

        assertThat(response).isEqualTo(new ErrorResponse("No books in specified date-range registered!"));
    }


// **********************     BOOKSALES    ************************

    @Test
    @DisplayName("Handle OverrideBookSalesRequest")
    void handleOverrideBookSalesRequest() {
        BookSales bookSales = Instancio.create(BookSales.class);

        OverrideBookSalesRequest overrideBookSalesRequest = new OverrideBookSalesRequest(bookSales);
        Response response = controller.handleRequest(overrideBookSalesRequest);

        assertThat(response).isEqualTo(new GeneralSuccessResponse());
    }

    @Test
    @DisplayName("Handle IncreaseSalesOfBookRequest")
    void handleIncreaseSalesOfBookRequest() {
        BookSales bookSales = new BookSales(1, 1);
        when(bookSalesRepository.getBookSalesByBookID(1)).thenReturn(bookSales);

        IncreaseSalesOfBookRequest increaseSalesOfBookRequest = new IncreaseSalesOfBookRequest(1, 1);
        Response response = controller.handleRequest(increaseSalesOfBookRequest);

        assertThat(response).isEqualTo(new GeneralSuccessResponse());
        verify(bookSalesRepository, times(1)).increaseSalesOfBook(1, 1);
    }

    @Test
    @DisplayName("Handle GetBookSalesRequest")
    void handleGetBookSalesRequest() {
        BookSales bookSales = new BookSales(1,10);
        when(bookSalesRepository.getBookSalesByBookID(1)).thenReturn(bookSales);

        GetBookSalesRequest getBookSalesRequest = new GetBookSalesRequest(1);
        Response response = controller.handleRequest(getBookSalesRequest);

        assertThat(response).isEqualTo(new GetBookSalesResponse(bookSales));
    }

    @Test
    @DisplayName("Handle GetBookSalesRequest with non-existing id")
    void handleGetBookSalesRequestWithNonExistingId() {
        when(bookSalesRepository.getBookSalesByBookID(1)).thenReturn(null);

        GetBookSalesRequest getBookSalesRequest = new GetBookSalesRequest(1);
        Response response = controller.handleRequest(getBookSalesRequest);

        assertThat(response).isEqualTo(new ErrorResponse("BookSales not found!"));
    }

    // **********************     OFFER    ************************

    @Test
    @DisplayName("Handle CreateOfferRequest")
    void handleCreateOfferRequest() {
        Offer offer = Instancio.create(Offer.class);

        CreateOfferRequest createOfferRequest = new CreateOfferRequest(offer);
        Response response = controller.handleRequest(createOfferRequest);

        assertThat(response).isEqualTo(new GeneralSuccessResponse());
    }

    @Test
    @DisplayName("Handle DeleteOfferRequest")
    void handleDeleteOfferRequest() {
        Offer offer = Instancio.create(Offer.class);
        when(offerRepository.getOfferById(1)).thenReturn(offer);

        DeleteOfferRequest deleteOfferRequest = new DeleteOfferRequest(1);
        Response response = controller.handleRequest(deleteOfferRequest);

        assertThat(response).isEqualTo(new GeneralSuccessResponse());
    }

    @Test
    @DisplayName("Handle DeleteOfferRequest with non-existing offer id")
    void handleDeleteOfferRequestWithNonExistingOfferId() {
        when(offerRepository.getOfferById(1)).thenReturn(null);

        DeleteOfferRequest deleteOfferRequest = new DeleteOfferRequest(1);
        Response response = controller.handleRequest(deleteOfferRequest);

        assertThat(response).isEqualTo(new ErrorResponse("Offer with specified id does NOT exist!"));
    }

    @Test
    @DisplayName("Handle GetOfferByIdRequest")
    void handleGetOfferByIdRequest() {
        Offer offer = Instancio.create(Offer.class);
        when(offerRepository.getOfferById(1)).thenReturn(offer);

        GetOfferByIdRequest getOfferByIdRequest = new GetOfferByIdRequest(1);
        Response response = controller.handleRequest(getOfferByIdRequest);

        assertThat(response).isEqualTo(new GetOfferResponse(offer));
    }

    @Test
    @DisplayName("Handle GetAllOffersRequest")
    void handleGetAllOffersRequest() {
        List<Offer> offers = Instancio.ofList(Offer.class).size(5).create();
        when(offerRepository.getAllOffers()).thenReturn(offers);

        GetAllOffersRequest getAllOffersRequest = new GetAllOffersRequest();
        Response response = controller.handleRequest(getAllOffersRequest);

        assertThat(response).isEqualTo(new GetOffersResponse(offers));
    }

    @Test
    @DisplayName("Handle GetAllOffersRequest with no offers to return")
    void handleGetAllOffersRequestWithNoOffersToReturn() {
        when(offerRepository.getAllOffers()).thenReturn(new ArrayList<>());

        GetAllOffersRequest getAllOffersRequest = new GetAllOffersRequest();
        Response response = controller.handleRequest(getAllOffersRequest);

        assertThat(response).isEqualTo(new ErrorResponse("No offers are registered!"));
    }

    @Test
    @DisplayName("Handle GetOffersByTagsRequest")
    void handleGetOffersByTagsRequest() {
        List<String> tags = List.of("Adventure", "Philosophy");
        List<Offer> offers = Instancio.ofList(Offer.class).size(5).create();
        when(offerRepository.getOffersByTags(tags)).thenReturn(offers);

        GetOffersByTagsRequest getOffersByTagsRequest = new GetOffersByTagsRequest(tags);
        Response response = controller.handleRequest(getOffersByTagsRequest);

        assertThat(response).isEqualTo(new GetOffersResponse(offers));
    }

    @Test
    @DisplayName("Handle GetOffersByTagsRequest with no offers to return")
    void handleGetOffersByTagsRequestWithNoOffersToReturn() {
        List<String> tags = List.of("Adventure", "Philosophy");
        when(offerRepository.getOffersByTags(tags)).thenReturn(new ArrayList<>());

        GetOffersByTagsRequest getOffersByTagsRequest = new GetOffersByTagsRequest(tags);
        Response response = controller.handleRequest(getOffersByTagsRequest);

        assertThat(response).isEqualTo(new ErrorResponse("No offers with specified tags are registered!"));
    }


}