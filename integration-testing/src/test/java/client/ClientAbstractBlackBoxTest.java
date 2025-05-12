package client;

import gr.bookapp.common.AuditContext;
import gr.bookapp.common.AuditContextImpl;
import gr.bookapp.common.IdGenerator;
import gr.bookapp.log.CompositeLoggerFactory;
import gr.bookapp.log.ConsoleLogger;
import gr.bookapp.log.FileLogger;
import gr.bookapp.log.Logger;
import gr.bookapp.repositories.*;
import gr.bookapp.services.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Disabled
public abstract class ClientAbstractBlackBoxTest { // TODO: Revisit name ( Integration test instead of blackBox )

    protected static AuditContext auditContext;
    protected static Clock clock;
    protected static Logger.Factory loggerFactory;
    protected static IdGenerator idGenerator;

    //Repos
    protected static BookRepository bookRepository;
    protected static BookSalesRepository bookSalesRepository;
    protected static UserRepository employeeRepository;
    protected static OfferRepository offerRepository;

    //Services
    protected static BookService bookService;
    protected static OfferService offerService;
    protected static UserService userService;
    protected static BookSalesService bookSalesService;
    protected static AuthenticationService authenticationService;

    private static @TempDir Path dir;

    @BeforeAll
    static void setup() throws IOException {
        auditContext = new AuditContextImpl();
        clock = Clock.fixed(LocalDate.of(2002,2,22).atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
        loggerFactory = new CompositeLoggerFactory(new ConsoleLogger(), new FileLogger(dir.resolve("fileLoggerBlackBoxTest")));
        idGenerator = new IdGenerator();

        //Services
        bookSalesService = new BookSalesService(bookSalesRepository, loggerFactory);
        bookService = new BookService(bookRepository, bookSalesRepository, loggerFactory);
        offerService = new OfferService(offerRepository, clock, loggerFactory);
        userService = new UserService(employeeRepository, bookRepository, offerService, bookSalesService, loggerFactory);
        authenticationService = new AuthenticationService(employeeRepository, loggerFactory);
    }

}
