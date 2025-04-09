package gr.bookapp.services;

import gr.bookapp.common.AuditContext;
import gr.bookapp.common.InstantFormatter;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.log.Logger;
import gr.bookapp.models.Book;
import gr.bookapp.models.Offer;
import gr.bookapp.repositories.BookRepository;
import gr.bookapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    BookRepository bookRepository = Mockito.mock(BookRepository.class);
    BookSalesService bookSalesService = Mockito.mock(BookSalesService.class);
    OfferService offerService = Mockito.mock(OfferService.class);
    AuditContext auditContext = Mockito.mock(AuditContext.class);
    Clock clock = Mockito.mock(Clock.class);
    Logger.Factory logger = Mockito.mock(Logger.Factory.class);
    UserService userService;

    @BeforeEach
    void initialize(){
        when(clock.instant()).thenReturn(Clock.systemUTC().instant());

        when(logger.create("User_Service")).thenReturn(Mockito.mock(Logger.class));
        userService = new UserService(userRepository, bookRepository, offerService, bookSalesService, logger);
    }

    @Test
    @DisplayName("Sell Book without offer test")
    void sellBookTest() throws InvalidInputException {
        long bookID = 100L;
        List<String> authors = List.of("Omiros");
        List<String> tags = List.of("Philosophy", "Adventure");
        ZonedDateTime zonedDateTime = LocalDate.of(-300, 1, 1).atStartOfDay(ZoneId.of("UTC"));
        Book book = new Book(bookID, "Odyssey", authors, 100, zonedDateTime.toInstant(), tags);
        when(bookRepository.getBookByID(bookID)).thenReturn(book);
        when(offerService.getOffers(authors)).thenReturn(new ArrayList<>());
        when(auditContext.getUserID()).thenReturn(999L);

        assertThat(userService.sellBook(bookID)).isEqualTo(book);
        verify(bookSalesService, times(1)).increaseSalesOfBook(bookID);
    }

    @Test
    @DisplayName("Sell a book with offer")
    void sellABookWithOffer() throws InvalidInputException {
        long bookID = 100L;
        ZonedDateTime zonedDateTime = LocalDate.of(-300, 1, 1).atStartOfDay(ZoneId.of("UTC"));
        List<String> authors = List.of("Omiros");
        List<String> tags = List.of("Philosophy", "Adventure");
        Book book = new Book(bookID, "Odyssey", authors, 100, zonedDateTime.toInstant(), tags);
        Offer offer = new Offer(700, tags,15, clock.instant().plus(5, ChronoUnit.DAYS));

        when(bookRepository.getBookByID(bookID)).thenReturn(book);
        when(offerService.getOffers(tags)).thenReturn(List.of(offer));
        when(auditContext.getUserID()).thenReturn(999L);
        assertThat(userService.sellBook(bookID)).isEqualTo(book.withPrice(book.price() - (book.price() * 15 / 100.0)));
        verify(bookSalesService, times(1)).increaseSalesOfBook(bookID);
    }

    @Test
    @DisplayName("Sell a book with multiple offers")
    void sellABookWithMultipleOffers() throws InvalidInputException {
        long bookID = 100L;
        ZonedDateTime zonedDateTime = LocalDate.of(-300, 1, 1).atStartOfDay(ZoneId.of("UTC"));
        List<String> authors = List.of("Omiros");
        List<String> tags = List.of("Philosophy", "Adventure");
        Book book = new Book(bookID, "Odyssey", authors, 100, zonedDateTime.toInstant(), tags);

        Offer offer1 = new Offer(700, List.of("Philosophy"),15, clock.instant().plus(5, ChronoUnit.DAYS));
        Offer offer2 = new Offer(800, List.of("Adventure"), 35, clock.instant().plus(9, ChronoUnit.DAYS));
        Offer expiredOffer = new Offer(900, List.of("Adventure"), 70, clock.instant().minus(1, ChronoUnit.DAYS));
        List<Offer> offers = List.of(offer1, offer2, expiredOffer);

        when(bookRepository.getBookByID(bookID)).thenReturn(book);
        when(offerService.getOffers(tags)).thenReturn(offers);
        when(auditContext.getUserID()).thenReturn(999L);
        assertThat(userService.sellBook(bookID)).isEqualTo(book.withPrice(book.price() - (book.price() * offer2.percentage() / 100.0)));
        verify(bookSalesService, times(1)).increaseSalesOfBook(bookID);

    }

    @Test
    @DisplayName("Sell book with invalid bookID")
    void sellBookWithInvalidBookId() {
        when(bookRepository.getBookByID(anyLong())).thenReturn(null);
        assertThrows(InvalidInputException.class, () -> userService.sellBook(0));
    }


}