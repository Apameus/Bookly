package gr.bookapp;

import gr.bookapp.common.*;
import gr.bookapp.database.Database;
import gr.bookapp.exceptions.ConfigurationFileLoadException;
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
import gr.bookapp.ui.TerminalUI;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;

public final class App {


    public static void main(String[] args) throws IOException, ConfigurationFileLoadException {
        ConfigLoader configLoader = new ConfigLoader(args[0]); //TODO
        BooklyConfig booklyConfig = configLoader.get();

        AuditContext auditContext = new AuditContextImpl();
        Clock clock = Clock.systemUTC();
        Logger.Factory loggerFactory = new CompositeLoggerFactory(new ConsoleLogger(), new FileLogger(booklyConfig.logsPath()));
        IdGenerator idGenerator = new IdGenerator();

        LongCodec longCodec = new LongCodec();
        StringCodec stringCodec = new StringCodec();
        ListCodec<String> listCodec = new ListCodec<>(stringCodec);
        DoubleCodec doubleCodec = new DoubleCodec();
        InstantCodec instantCodec = new InstantCodec(stringCodec);
        BookCodec bookCodec = new BookCodec(stringCodec, listCodec, instantCodec);
        BookSalesCodec bookSalesCodec = new BookSalesCodec();
        EmployeeCodec employeeCodec = new EmployeeCodec(stringCodec);
        OfferCodec offerCodec = new OfferCodec(listCodec, instantCodec);
        AuditCodec auditCodec = new AuditCodec(new StringCodec(100), instantCodec);

        //DBs
        FileBasedNodeStorageTree<Long, Book> bookNodeStorage = new FileBasedNodeStorageTree<>(booklyConfig.booksPath(), longCodec, bookCodec);
        ObjectTable<Long, Book> bookObjectTable = new BinarySearchTree<>(Long::compareTo, bookNodeStorage);
        Database<Long, Book> bookDataBase = new Database<>(bookObjectTable);

        FileBasedNodeStorageTree<Long, BookSales> bookSalesNodeStorage = new FileBasedNodeStorageTree<>(booklyConfig.bookSalesPath(), longCodec, bookSalesCodec);
        ObjectTable <Long, BookSales> bookSalesObjectTable = new BinarySearchTree<>(Long::compareTo, bookSalesNodeStorage);
        Database<Long, BookSales> bookSalesDataBase = new Database<>(bookSalesObjectTable);

        FileBasedNodeStorageTree<Long, User> employeeNodeStorage = new FileBasedNodeStorageTree<>(booklyConfig.employeesPath(), longCodec, employeeCodec);
        ObjectTable <Long, User> employeeObjectTable = new BinarySearchTree<>(Long::compareTo, employeeNodeStorage);
        Database<Long, User> employeeDataBase = new Database<>(employeeObjectTable);

        FileBasedNodeStorageTree<Long, Offer> offerNodeStorage = new FileBasedNodeStorageTree<>(booklyConfig.offersPath(), longCodec, offerCodec);
        ObjectTable <Long, Offer> offerObjectTable = new BinarySearchTree<>(Long::compareTo, offerNodeStorage);
        Database<Long, Offer> offerDataBase = new Database<>(offerObjectTable);

        FileBasedNodeStorageTree<Instant, Audit> auditNodeStorage = new FileBasedNodeStorageTree<>(booklyConfig.auditsPath(), instantCodec, auditCodec);
        ObjectTable <Instant, Audit> auditObjectTable = new BinarySearchTree<>(Instant::compareTo, auditNodeStorage);
        Database<Instant, Audit> auditDataBase = new Database<>(auditObjectTable);


        //Repos
        BookRepository bookRepository = new BookRepository(bookDataBase);
        BookSalesRepository bookSalesRepository = new BookSalesRepository(bookSalesDataBase);
        AuditRepository auditRepository = new AuditRepository(auditDataBase);
        UserRepository userRepository = new UserRepository(employeeDataBase);
        OfferRepository offerRepository = new OfferRepository(offerDataBase);

        //Services
        BookSalesService bookSalesService = new BookSalesService(bookSalesRepository, loggerFactory);
        BookService bookService = new BookService(bookRepository, bookSalesRepository, auditRepository, auditContext, clock, loggerFactory);
        OfferService offerService = new OfferService(offerRepository, idGenerator, auditRepository, auditContext, clock, loggerFactory);
        UserService userService = new UserService(userRepository, bookRepository, auditRepository, offerService, bookSalesService, auditContext, clock, loggerFactory);
        AdminService adminService = new AdminService(userRepository, idGenerator, loggerFactory);
        AuthenticationService authenticationService = new AuthenticationService(userRepository, loggerFactory);

        CsvParser csvParser = new CsvParser(bookService, bookSalesRepository, userRepository, offerRepository);

        //UI
        TerminalUI terminalUI = new TerminalUI(idGenerator, authenticationService, userService, userRepository, adminService, bookService, csvParser);
        terminalUI.start();
    }
}
