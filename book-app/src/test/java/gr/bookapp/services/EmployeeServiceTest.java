package gr.bookapp.services;

import gr.bookapp.common.AuditContext;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.Book;
import gr.bookapp.models.Offer;
import gr.bookapp.repositories.AuditRepository;
import gr.bookapp.repositories.BookRepository;
import gr.bookapp.repositories.EmployeeRepository;
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

class EmployeeServiceTest {
    EmployeeRepository employeeRepository = Mockito.mock(EmployeeRepository.class);
    BookRepository bookRepository = Mockito.mock(BookRepository.class);
    AuditRepository auditRepository = Mockito.mock(AuditRepository.class);
    BookSalesService bookSalesService = Mockito.mock(BookSalesService.class);
    OfferService offerService = Mockito.mock(OfferService.class);
    AuditContext auditContext = Mockito.mock(AuditContext.class);
    Clock clock = Mockito.mock(Clock.class);
    EmployeeService employeeService;

    @BeforeEach
    void initialize(){
        ZoneId zone = ZoneId.of("UTC");
        when(clock.instant()).thenReturn(Clock.systemUTC().instant());
        when(clock.getZone()).thenReturn(zone);
        employeeService = new EmployeeService(employeeRepository, bookRepository, auditRepository, offerService, bookSalesService, auditContext, clock);
    }

    @Test
    @DisplayName("Sell Book without offer test")
    void sellBookTest() throws InvalidInputException {
        long bookID = 100L;
        ZonedDateTime zonedDateTime = LocalDate.of(-300, 1, 1).atStartOfDay(ZoneId.of("UTC"));
        List<String> tags = List.of("Omiros");
        Book book = new Book(1, "Odyssey", tags, 100, zonedDateTime.toInstant(), List.of("Philosophy", "Adventure"));
        when(bookRepository.getBookByID(bookID)).thenReturn(book);
        when(offerService.getOffers(tags)).thenReturn(new ArrayList<>());
        when(auditContext.getEmployeeID()).thenReturn(999L);
        employeeService.sellBook(bookID);
        verify(bookSalesService, times(1)).increaseSalesOfBook(bookID);
        verify(auditRepository, times(1)).audit(999, "Book sailed", clock.instant());
    }

    @Test
    @DisplayName("Sell a book with offer")
    void sellABookWithOffer() throws InvalidInputException {
        long bookID = 100L;
        ZonedDateTime zonedDateTime = LocalDate.of(-300, 1, 1).atStartOfDay(ZoneId.of("UTC"));
        List<String> authors = List.of("Omiros");
        List<String> tags = List.of("Philosophy", "Adventure");
        Book book = new Book(1, "Odyssey", authors, 100, zonedDateTime.toInstant(), tags);
        Offer offer = new Offer(700, tags,15, clock.instant().plus(5, ChronoUnit.DAYS));

        when(bookRepository.getBookByID(bookID)).thenReturn(book);
        when(offerService.getOffers(tags)).thenReturn(List.of(offer));
        when(auditContext.getEmployeeID()).thenReturn(999L);
        assertThat(employeeService.sellBook(bookID)).isEqualTo(book.withPrice(book.price() - (book.price() * 15 / 100.0)));
        verify(bookSalesService, times(1)).increaseSalesOfBook(bookID);
        verify(auditRepository, times(1)).audit(999, "Book with id: %s sold with extra offer of: %s from offer with id: %s".formatted(bookID, offer.percentage(), offer.offerID()), clock.instant());
    }

    @Test
    @DisplayName("Sell book with invalid bookID")
    void sellBookWithInvalidBookId() {
        assertThrows(InvalidInputException.class, () -> employeeService.sellBook(0));
    }


}