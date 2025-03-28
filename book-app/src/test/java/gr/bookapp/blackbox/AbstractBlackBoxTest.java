package gr.bookapp.blackbox;

import gr.bookapp.common.AuditContext;
import gr.bookapp.common.AuditContextImpl;
import gr.bookapp.common.IdGenerator;
import gr.bookapp.database.Database;
import gr.bookapp.log.CompositeLoggerFactory;
import gr.bookapp.log.ConsoleLogger;
import gr.bookapp.log.FileLogger;
import gr.bookapp.log.Logger;
import gr.bookapp.models.*;
import gr.bookapp.repositories.*;
import gr.bookapp.services.*;
import gr.bookapp.storage.codec.*;
import gr.bookapp.storage.file.BinarySearchTree;
import gr.bookapp.storage.file.FileBasedNodeStorageTree;
import gr.bookapp.storage.file.ObjectTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public abstract class AbstractBlackBoxTest {
    AuditContext auditContext;
    Clock clock;
    Logger.Factory loggerFactory;
    IdGenerator idGenerator;

    LongCodec longCodec;
    StringCodec stringCodec;
    ListCodec<String> listCodec;
    DoubleCodec doubleCodec;
    InstantCodec instantCodec;
    BookCodec bookCodec;
    BookSalesCodec bookSalesCodec;
    EmployeeCodec employeeCodec;
    OfferCodec offerCodec;
    AuditCodec auditCodec;

    //DBs
    FileBasedNodeStorageTree<Long, Book> bookNodeStorage;
    ObjectTable<Long, Book> bookObjectTable;
    Database<Long, Book> bookDataBase;

    FileBasedNodeStorageTree<Long, BookSales> bookSalesNodeStorage;
    ObjectTable <Long, BookSales> bookSalesObjectTable;
    Database<Long, BookSales> bookSalesDataBase;

    FileBasedNodeStorageTree<Long, User> employeeNodeStorage;
    ObjectTable <Long, User> employeeObjectTable;
    Database<Long, User> employeeDataBase;

    FileBasedNodeStorageTree<Long, Offer> offerNodeStorage;
    ObjectTable <Long, Offer> offerObjectTable;
    Database<Long, Offer> offerDataBase;

    FileBasedNodeStorageTree<Instant, Audit> auditNodeStorage;
    ObjectTable <Instant, Audit> auditObjectTable;
    Database<Instant, Audit> auditDataBase;


    //Repos
    BookRepository bookRepository;
    BookSalesRepository bookSalesRepository;
    AuditRepository auditRepository;
    UserRepository employeeRepository;
    OfferRepository offerRepository;

    //Services
    protected   BookService bookService;
    protected   OfferService offerService;
    protected UserService employeeService;
    protected   BookSalesService bookSalesService;
    protected   AuthenticationService authenticationService;

    @BeforeEach
    void setup(@TempDir Path dir) throws IOException {
        auditContext = new AuditContextImpl();
        clock = Clock.fixed(LocalDate.of(2002,2,22).atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
        loggerFactory = new CompositeLoggerFactory(new ConsoleLogger(), new FileLogger(dir.resolve("fileLoggerBlackBoxTest")));
        idGenerator = new IdGenerator();

        longCodec = new LongCodec();
        stringCodec = new StringCodec();
        listCodec = new ListCodec<>(stringCodec);
        doubleCodec = new DoubleCodec();
        instantCodec = new InstantCodec(stringCodec);
        bookCodec = new BookCodec(stringCodec, listCodec, instantCodec);
        bookSalesCodec = new BookSalesCodec();
        employeeCodec = new EmployeeCodec(stringCodec);
        offerCodec = new OfferCodec(listCodec, instantCodec);
        auditCodec = new AuditCodec(new StringCodec(100), instantCodec);

        //DBs
        bookNodeStorage = new FileBasedNodeStorageTree<>(dir.resolve("Books"), longCodec, bookCodec);
        bookObjectTable = new BinarySearchTree<>(Long::compareTo, bookNodeStorage);
        bookDataBase = new Database<>(bookObjectTable);

        bookSalesNodeStorage = new FileBasedNodeStorageTree<>(dir.resolve("BookSales"), longCodec, bookSalesCodec);
        bookSalesObjectTable = new BinarySearchTree<>(Long::compareTo, bookSalesNodeStorage);
        bookSalesDataBase = new Database<>(bookSalesObjectTable);

        employeeNodeStorage = new FileBasedNodeStorageTree<>(dir.resolve("Users"), longCodec, employeeCodec);
        employeeObjectTable = new BinarySearchTree<>(Long::compareTo, employeeNodeStorage);
        employeeDataBase = new Database<>(employeeObjectTable);

        offerNodeStorage = new FileBasedNodeStorageTree<>(dir.resolve("Offers"), longCodec, offerCodec);
        offerObjectTable = new BinarySearchTree<>(Long::compareTo, offerNodeStorage);
        offerDataBase = new Database<>(offerObjectTable);

        auditNodeStorage = new FileBasedNodeStorageTree<>(dir.resolve("Audits"), instantCodec, auditCodec);
        auditObjectTable = new BinarySearchTree<>(Instant::compareTo, auditNodeStorage);
        auditDataBase = new Database<>(auditObjectTable);


        //Repos
        bookRepository = new BookRepository(bookDataBase);
        bookSalesRepository = new BookSalesRepository(bookSalesDataBase);
        auditRepository = new AuditRepository(auditDataBase);
        employeeRepository = new UserRepository(employeeDataBase);
        offerRepository = new OfferRepository(offerDataBase);

        //Services
        AuditService auditService = new AuditService(auditRepository, auditContext);
        bookSalesService = new BookSalesService(bookSalesRepository, loggerFactory);
        bookService = new BookService(bookRepository, bookSalesRepository, auditService, loggerFactory);
        offerService = new OfferService(offerRepository, idGenerator, auditService, loggerFactory);
        employeeService = new UserService(employeeRepository, bookRepository, offerService, bookSalesService, auditService, loggerFactory);
        authenticationService = new AuthenticationService(employeeRepository, loggerFactory);
    }

}
