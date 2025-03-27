package gr.bookapp.services;

import gr.bookapp.common.AuditContext;
import gr.bookapp.log.Logger;
import gr.bookapp.models.Book;
import gr.bookapp.repositories.AuditRepository;
import gr.bookapp.repositories.BookRepository;
import gr.bookapp.repositories.BookSalesRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import java.time.*;
import java.util.List;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.class)
class BookServiceTest {
    BookRepository bookRepository = Mockito.mock(BookRepository.class);
    BookSalesRepository bookSalesRepository = Mockito.mock(BookSalesRepository.class);
    AuditRepository auditRepository = Mockito.mock(AuditRepository.class);
    AuditContext auditContext = Mockito.mock(AuditContext.class);
    Clock clock = Mockito.mock(Clock.class);
    Logger.Factory logger = Mockito.mock(Logger.Factory.class);
    BookService bookService;

    @BeforeEach
    void initialize(){
        bookService = new BookService(bookRepository, bookSalesRepository, auditRepository, auditContext, clock, logger);
    }

    @Test
    @Order(0)
    @DisplayName("Add book test")
    void addBookTest() {
        Instant fixedInstant = Instant.parse("2030-01-01T00:00:00Z");
        when(clock.instant()).thenReturn(fixedInstant);

        ZonedDateTime zonedDateTime = LocalDate.of(-300, 1, 1).atStartOfDay(ZoneId.of("UTC"));
        Book book = new Book(1, "Odyssea", List.of("Omiros"), 100, zonedDateTime.toInstant(), List.of("Philosophy", "Adventure"));

        bookService.addBook(book);
        verify(bookRepository, times(1)).add(book);
    }

    @Test
    @Order(1)
    @DisplayName("Delete book test")
    void deleteBookTest() {
        bookService.deleteBookByID(1);
        verify(bookRepository, times(1)).deleteBookByID(1);
        verify(auditRepository, times(1)).audit(auditContext.getUserID(), "Book with id 1 deleted", clock.instant());
    }


}