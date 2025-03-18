package gr.bookapp.ui;

import gr.bookapp.common.AuditContext;
import gr.bookapp.common.IdGenerator;
import gr.bookapp.repositories.AuditRepository;
import gr.bookapp.repositories.BookRepository;
import gr.bookapp.repositories.BookSalesRepository;
import gr.bookapp.repositories.EmployeeRepository;
import gr.bookapp.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.Console;
import java.time.Clock;

//@ExtendWith(MockitoExtension.class)
class TerminalUITest {
    IdGenerator idGenerator = new IdGenerator();
    Console console = Mockito.mock(Console.class);
    Clock clock = Clock.systemUTC();
    AuditContext auditContext = Mockito.mock(AuditContext.class);

    BookRepository bookRepository = Mockito.mock(BookRepository.class);
    EmployeeRepository employeeRepository = Mockito.mock(EmployeeRepository.class);
    AuditRepository auditRepository = Mockito.mock(AuditRepository.class);
    OfferService offerService = Mockito.mock(OfferService.class);
    BookSalesService bookSalesService = Mockito.mock(BookSalesService.class);

    AuthenticationService authenticationService;
    EmployeeService employeeService;
    BookService bookService;
    TerminalUI terminalUI;

    @BeforeEach
    void initialize(){
        authenticationService = new AuthenticationService(employeeRepository);
        employeeService = new EmployeeService(employeeRepository, bookRepository, auditRepository, offerService, bookSalesService, auditContext, clock);
        bookService = new BookService(bookRepository, auditRepository, auditContext, clock);
        terminalUI = new TerminalUI(idGenerator, authenticationService, employeeService, bookService);
    }

    @Test
    @DisplayName("Test")
    void test() {
        terminalUI.start();
    }
}