package gr.bookapp.services;

import gr.bookapp.common.AuditContext;
import gr.bookapp.models.Book;
import gr.bookapp.repositories.AuditRepository;
import gr.bookapp.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.time.*;
import java.util.List;
import static org.mockito.Mockito.*;

class BookServiceTest {
    BookRepository bookRepository = Mockito.mock(BookRepository.class);
    AuditRepository auditRepository = Mockito.mock(AuditRepository.class);
    AuditContext auditContext = Mockito.mock(AuditContext.class);
    Clock clock = Mockito.mock(Clock.class);
    BookService bookService;

    @BeforeEach
    void initialize(){
        bookService = new BookService(bookRepository, auditRepository, auditContext, clock);
    }

    @Test
    @DisplayName("Add book test")
    void addBookTest() {
        Instant fixedInstant = Instant.parse("2030-01-01T00:00:00Z");
        ZoneId zoneID = ZoneId.of("UTC");
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(zoneID);
        ZonedDateTime zonedDateTime = LocalDate.of(-300, 1, 1).atStartOfDay(zoneID);
        Book book = new Book(1, "Odyssea", List.of("Omiros"), 100, zonedDateTime.toInstant(), List.of("Philosophy", "Adventure"));
        bookService.addBook(book);
        verify(bookRepository, times(1)).add(book);
    }
}