package gr.bookapp;

import gr.bookapp.common.AuditContext;
import gr.bookapp.common.AuditContextImpl;
import gr.bookapp.common.IdGenerator;
import gr.bookapp.database.Database;
import gr.bookapp.models.*;
import gr.bookapp.repositories.*;
import gr.bookapp.services.*;
import gr.bookapp.storage.codec.*;
import gr.bookapp.storage.file.BinarySearchTree;
import gr.bookapp.storage.file.FileBasedNodeStorageTree;
import gr.bookapp.storage.file.ObjectTable;
import gr.bookapp.ui.TerminalUI;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

public final class App {
    public static void main(String[] args) throws IOException {
        IdGenerator idGenerator = new IdGenerator();
        Clock clock = Clock.systemUTC();
        AuditContext auditContext = new AuditContextImpl();

        LongCodec longCodec = new LongCodec();
        StringCodec stringCodec = new StringCodec();
        ListCodec<String> listCodec = new ListCodec<>(stringCodec);
        DoubleCodec doubleCodec = new DoubleCodec();
        InstantCodec instantCodec = new InstantCodec(longCodec);
        BookCodec bookCodec = new BookCodec(stringCodec, listCodec, instantCodec);
        BookSalesCodec bookSalesCodec = new BookSalesCodec();
        EmployeeCodec employeeCodec = new EmployeeCodec(stringCodec);
        OfferCodec offerCodec = new OfferCodec(listCodec, instantCodec);
        AuditCodec auditCodec = new AuditCodec(stringCodec, instantCodec);

        //DBs
        FileBasedNodeStorageTree<Long, Book> bookNodeStorage = new FileBasedNodeStorageTree<>(Path.of(""), longCodec, bookCodec);
        ObjectTable<Long, Book> bookObjectTable = new BinarySearchTree<>(Long::compareTo, bookNodeStorage);
        Database<Long, Book> bookDataBase = new Database<>(bookObjectTable);

        FileBasedNodeStorageTree<Long, BookSales> bookSalesNodeStorage = new FileBasedNodeStorageTree<>(Path.of(""), longCodec, bookSalesCodec);
        ObjectTable <Long, BookSales> bookSalesObjectTable = new BinarySearchTree<>(Long::compareTo, bookSalesNodeStorage);
        Database<Long, BookSales> bookSalesDataBase = new Database<>(bookSalesObjectTable);

        FileBasedNodeStorageTree<Long, Employee> employeeNodeStorage = new FileBasedNodeStorageTree<>(Path.of(""), longCodec, employeeCodec);
        ObjectTable <Long, Employee> employeeObjectTable = new BinarySearchTree<>(Long::compareTo, employeeNodeStorage);
        Database<Long, Employee> employeeDataBase = new Database<>(employeeObjectTable);

        FileBasedNodeStorageTree<Long, Offer> offerNodeStorage = new FileBasedNodeStorageTree<>(Path.of(""), longCodec, offerCodec);
        ObjectTable <Long, Offer> offerObjectTable = new BinarySearchTree<>(Long::compareTo, offerNodeStorage);
        Database<Long, Offer> offerDataBase = new Database<>(offerObjectTable);

        FileBasedNodeStorageTree<Instant, Audit> auditNodeStorage = new FileBasedNodeStorageTree<>(Path.of(""), instantCodec, auditCodec);
        ObjectTable <Instant, Audit> auditObjectTable = new BinarySearchTree<>(Instant::compareTo, auditNodeStorage);
        Database<Instant, Audit> auditDataBase = new Database<>(auditObjectTable);


        //Repos
        BookRepository bookRepository = new BookRepository(bookDataBase);
        BookSalesRepository bookSalesRepository = new BookSalesRepository(bookSalesDataBase);
        AuditRepository auditRepository = new AuditRepository(auditDataBase);
        EmployeeRepository employeeRepository = new EmployeeRepository(employeeDataBase);
        OfferRepository offerRepository = new OfferRepository(offerDataBase);

        //Services
        BookService bookService = new BookService(bookRepository, auditRepository, auditContext, clock);
        BookSalesService bookSalesService = new BookSalesService(bookSalesRepository);
        OfferService offerService = new OfferService(offerRepository, idGenerator, auditRepository, auditContext, clock);
        EmployeeService employeeService = new EmployeeService(employeeRepository, bookRepository, auditRepository, offerService, bookSalesService, auditContext, clock);
        AuthenticationService authenticationService = new AuthenticationService(employeeRepository);

        //UI
        TerminalUI terminalUI = new TerminalUI(idGenerator, authenticationService, employeeService, bookService);
        terminalUI.start();
    }
}
