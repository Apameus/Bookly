package gr.bookapp;

import gr.bookapp.common.*;
import gr.bookapp.csv.CsvLoader;
import gr.bookapp.services.CsvService;
import gr.bookapp.exceptions.ConfigurationFileLoadException;
import gr.bookapp.log.CompositeLoggerFactory;
import gr.bookapp.log.ConsoleLogger;
import gr.bookapp.log.FileLogger;
import gr.bookapp.log.Logger;
import gr.bookapp.repositories.*;
import gr.bookapp.services.*;
import gr.bookapp.ui.AdminPanelUI;
import gr.bookapp.ui.EmployeePanel;
import gr.bookapp.ui.LoginPanelUI;
import gr.bookapp.ui.TerminalUI;
import java.io.IOException;
import java.time.Clock;

public final class AppClient {


    public static void main(String[] args) throws IOException, ConfigurationFileLoadException {
        if (args.length == 0) throw new IllegalStateException("You need to specify the config file path !");
        ConfigLoader configLoader = new ConfigLoader(args[0]); //TODO
        BooklyConfig booklyConfig = configLoader.get();

        AuditContext auditContext = new AuditContextImpl();
        Clock clock = Clock.systemUTC();
        Logger.Factory loggerFactory = new CompositeLoggerFactory(new ConsoleLogger(), new FileLogger(booklyConfig.logsPath()));
        IdGenerator idGenerator = new IdGenerator();

//        LongCodec longCodec = new LongCodec();
//        StringCodec stringCodec = new StringCodec();
//        ListCodec<String> listCodec = new ListCodec<>(stringCodec);
//        DoubleCodec doubleCodec = new DoubleCodec();
//        InstantCodec instantCodec = new InstantCodec(longCodec);
//        BookCodec bookCodec = new BookCodec(stringCodec, listCodec, instantCodec);
//        BookSalesCodec bookSalesCodec = new BookSalesCodec();
//        EmployeeCodec employeeCodec = new EmployeeCodec(stringCodec);
//        OfferCodec offerCodec = new OfferCodec(listCodec, instantCodec);
//        AuditCodec auditCodec = new AuditCodec(new StringCodec(100), instantCodec);

        //DBs
//        FileBasedNodeStorageTree<Long, Book> bookNodeStorage = new FileBasedNodeStorageTree<>(booklyConfig.booksPath(), longCodec, bookCodec);
//        ObjectTable<Long, Book> bookObjectTable = new BinarySearchTree<>(Long::compareTo, bookNodeStorage);
//        Database<Long, Book> bookDataBase = new Database<>(bookObjectTable);

//        FileBasedNodeStorageTree<Long, BookSales> bookSalesNodeStorage = new FileBasedNodeStorageTree<>(booklyConfig.bookSalesPath(), longCodec, bookSalesCodec);
//        ObjectTable <Long, BookSales> bookSalesObjectTable = new BinarySearchTree<>(Long::compareTo, bookSalesNodeStorage);
//        Database<Long, BookSales> bookSalesDataBase = new Database<>(bookSalesObjectTable);

//        FileBasedNodeStorageTree<Long, User> employeeNodeStorage = new FileBasedNodeStorageTree<>(booklyConfig.employeesPath(), longCodec, employeeCodec);
//        ObjectTable <Long, User> employeeObjectTable = new BinarySearchTree<>(Long::compareTo, employeeNodeStorage);
//        Database<Long, User> employeeDataBase = new Database<>(employeeObjectTable);

//        FileBasedNodeStorageTree<Long, Offer> offerNodeStorage = new FileBasedNodeStorageTree<>(booklyConfig.offersPath(), longCodec, offerCodec);
//        ObjectTable <Long, Offer> offerObjectTable = new BinarySearchTree<>(Long::compareTo, offerNodeStorage);
//        Database<Long, Offer> offerDataBase = new Database<>(offerObjectTable);

//        FileBasedNodeStorageTree<Instant, Audit> auditNodeStorage = new FileBasedNodeStorageTree<>(booklyConfig.auditsPath(), instantCodec, auditCodec);
//        ObjectTable <Instant, Audit> auditObjectTable = new BinarySearchTree<>(Instant::compareTo, auditNodeStorage);
//        Database<Instant, Audit> auditDataBase = new Database<>(auditObjectTable);


        //Repos
        BookRepository bookRepository = null;
        BookSalesRepository bookSalesRepository = null;
        AuditRepository auditRepository = null;
        UserRepository userRepository = null;
        OfferRepository offerRepository = null;

        //Services
        AuditService auditService = new AuditService(auditRepository, auditContext);
        BookSalesService bookSalesService = new BookSalesService(bookSalesRepository, loggerFactory);
        BookService bookService = new BookService(bookRepository, bookSalesRepository, auditService, loggerFactory);
        OfferService offerService = new OfferService(offerRepository, idGenerator, auditService, Clock.systemUTC(), loggerFactory);
        UserService userService = new UserService(userRepository, bookRepository, offerService, bookSalesService, auditService, loggerFactory);
        AdminService adminService = new AdminService(userRepository, auditService, idGenerator, loggerFactory);
        AuthenticationService authenticationService = new AuthenticationService(userRepository, loggerFactory);

        CsvService csvService = new CsvService(new CsvLoader(), bookService, bookSalesRepository, userRepository, offerRepository);

        //UI
        LoginPanelUI loginPanelUI = new LoginPanelUI(authenticationService, userService, userRepository, idGenerator);
        AdminPanelUI adminPanelUI = new AdminPanelUI(csvService, adminService);
        EmployeePanel employeePanel = new EmployeePanel(userService, bookService, idGenerator);
        TerminalUI terminalUI = new TerminalUI(loginPanelUI, adminPanelUI, employeePanel);
        terminalUI.start();
    }
}
