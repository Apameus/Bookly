package e2e;

import gr.bookapp.Audit;
import gr.bookapp.client.Client;
import gr.bookapp.client.ClientImpl;
import gr.bookapp.common.IdGenerator;
import gr.bookapp.database.Controller;
import gr.bookapp.database.Database;
import gr.bookapp.database.TCPServer;
import gr.bookapp.log.CompositeLoggerFactory;
import gr.bookapp.log.ConsoleLogger;
import gr.bookapp.log.Logger;
import gr.bookapp.models.Book;
import gr.bookapp.models.BookSales;
import gr.bookapp.models.Offer;
import gr.bookapp.models.User;
import gr.bookapp.protocol.codec.*;
import gr.bookapp.protocol.packages.RequestStreamCodec;
import gr.bookapp.protocol.packages.ResponseStreamCodec;
import gr.bookapp.repositories.*;
import gr.bookapp.services.BookSalesServiceDbImpl;
import gr.bookapp.services.BookServiceDbImpl;
import gr.bookapp.services.OfferServiceDbImpl;
import gr.bookapp.services.UserServiceDbImpl;
import gr.bookapp.storage.codec.AuditCodec;
import gr.bookapp.storage.file.BinarySearchTree;
import gr.bookapp.storage.file.FileBasedNodeStorageTree;
import gr.bookapp.storage.file.ObjectTable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.time.Instant;

public abstract class E2EAbstractTest {

    static Client client;

    private static @TempDir Path dir;

    //DBs
    static FileBasedNodeStorageTree<Long, Book> bookNodeStorage;
    static ObjectTable<Long, Book> bookObjectTable;
    static Database<Long, Book> bookDataBase;

    static FileBasedNodeStorageTree<Long, BookSales> bookSalesNodeStorage;
    static ObjectTable <Long, BookSales> bookSalesObjectTable;
    static Database<Long, BookSales> bookSalesDataBase;

    static FileBasedNodeStorageTree<Long, User> employeeNodeStorage;
    static ObjectTable<Long, User> employeeObjectTable;
    static Database<Long, User> userDataBase;

    static FileBasedNodeStorageTree<Long, Offer> offerNodeStorage;
    static ObjectTable <Long, Offer> offerObjectTable;
    static Database<Long, Offer> offerDataBase;

    static FileBasedNodeStorageTree<Instant, Audit> auditNodeStorage;
    static ObjectTable <Instant, Audit> auditObjectTable;
    static Database<Instant, Audit> auditDataBase;

    // Repos
    static UserRepository userRepository;
    static BookRepository bookRepository;
    static BookSalesRepository bookSalesRepository;
    static OfferRepository offerRepository;

    // Services
    static UserServiceDbImpl userServiceDb;
    static BookServiceDbImpl bookServiceDb;
    static BookSalesServiceDbImpl bookSalesServiceDb;
    static OfferServiceDbImpl offerServiceDb;

    // Server
    static TCPServer tcpServer;
    static Controller controller;
    static RequestStreamCodec requestStreamCodec;
    static SocketAddress address;
    static Logger.Factory logFactory;

    // Codecs
    static StringCodec stringCodec;
    static LongCodec longCodec;
    static InstantCodec instantCodec;
    static ListCodec<String> stringListCodec;
    static BookCodec bookCodec;
    static BookSalesCodec bookSalesCodec;
    static OfferCodec offerCodec;
    static UserCodec userCodec;
    static AuditCodec auditCodec;



    @BeforeAll
    static void setup() throws IOException {
        IdGenerator idGenerator = new IdGenerator();
        // Repositories
        userRepository = new UserRepositoryDbImpl(userDataBase, idGenerator);
        bookRepository = new BookRepositoryDbImpl(bookDataBase, idGenerator);
        bookSalesRepository = new BookSalesRepositoryDbImpl(bookSalesDataBase);
        offerRepository = new OfferRepositoryDbImpl(offerDataBase, idGenerator);
        // Services
        userServiceDb = new UserServiceDbImpl(userRepository);
        bookServiceDb = new BookServiceDbImpl(bookRepository);
        bookSalesServiceDb = new BookSalesServiceDbImpl(bookSalesRepository);
        offerServiceDb = new OfferServiceDbImpl(offerRepository);

        // Codecs
        stringCodec = new StringCodec();
        longCodec = new LongCodec();
        instantCodec = new InstantCodec();
        stringListCodec = new ListCodec<>(stringCodec);
        bookCodec = new BookCodec(stringCodec, stringListCodec, instantCodec);
        bookSalesCodec = new BookSalesCodec();
        offerCodec = new OfferCodec(stringListCodec, instantCodec);
        userCodec = new UserCodec(stringCodec);
        auditCodec = new AuditCodec(stringCodec, instantCodec);

        // StreamPackages
        requestStreamCodec = new RequestStreamCodec(bookCodec, bookSalesCodec, offerCodec, stringCodec, stringListCodec, instantCodec);

        address = new InetSocketAddress(8080);
        client = new ClientImpl(requestStreamCodec, new ResponseStreamCodec(), address);
        logFactory = new CompositeLoggerFactory(new ConsoleLogger());

        controller = new Controller(userServiceDb, bookServiceDb, bookSalesServiceDb, offerServiceDb);

        tcpServer = new TCPServer(address, logFactory, controller, requestStreamCodec);

        Thread.ofVirtual().start(() -> {
            try {
                tcpServer.run();
            } catch (Exception e) {}
        });
    }

    @BeforeAll
    static void setDbs() throws IOException {
        //DBs
        bookNodeStorage = new FileBasedNodeStorageTree<>(dir.resolve("Books"), longCodec, bookCodec);
        bookObjectTable = new BinarySearchTree<>(Long::compareTo, bookNodeStorage);
        bookDataBase = new Database<>(bookObjectTable);

        bookSalesNodeStorage = new FileBasedNodeStorageTree<>(dir.resolve("BookSales"), longCodec, bookSalesCodec);
        bookSalesObjectTable = new BinarySearchTree<>(Long::compareTo, bookSalesNodeStorage);
        bookSalesDataBase = new Database<>(bookSalesObjectTable);

        employeeNodeStorage = new FileBasedNodeStorageTree<>(dir.resolve("Users"), longCodec, userCodec);
        employeeObjectTable = new BinarySearchTree<>(Long::compareTo, employeeNodeStorage);
        userDataBase = new Database<>(employeeObjectTable);

        offerNodeStorage = new FileBasedNodeStorageTree<>(dir.resolve("Offers"), longCodec, offerCodec);
        offerObjectTable = new BinarySearchTree<>(Long::compareTo, offerNodeStorage);
        offerDataBase = new Database<>(offerObjectTable);

        auditNodeStorage = new FileBasedNodeStorageTree<>(dir.resolve("Audits"), instantCodec, auditCodec);
        auditObjectTable = new BinarySearchTree<>(Instant::compareTo, auditNodeStorage);
        auditDataBase = new Database<>(auditObjectTable);

    }

    @AfterAll
    static void stopServer() throws Exception {
        tcpServer.close();
    }}
