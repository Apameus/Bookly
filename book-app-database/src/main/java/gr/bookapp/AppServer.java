package gr.bookapp;

import gr.bookapp.common.IdGenerator;
import gr.bookapp.database.Controller;
import gr.bookapp.database.Database;
import gr.bookapp.database.TCPServer;
import gr.bookapp.exceptions.ConfigurationFileLoadException;
import gr.bookapp.log.CompositeLoggerFactory;
import gr.bookapp.log.FileLogger;
import gr.bookapp.log.Logger;
import gr.bookapp.models.Book;
import gr.bookapp.models.BookSales;
import gr.bookapp.models.Offer;
import gr.bookapp.models.User;
import gr.bookapp.protocol.codec.*;
import gr.bookapp.protocol.packages.RequestStreamCodec;
import gr.bookapp.repositories.*;
import gr.bookapp.services.BookSalesServiceDbImpl;
import gr.bookapp.services.BookServiceDbImpl;
import gr.bookapp.services.OfferServiceDbImpl;
import gr.bookapp.services.UserServiceDbImpl;
import gr.bookapp.storage.codec.AuditCodec;
import gr.bookapp.storage.file.BinarySearchTree;
import gr.bookapp.storage.file.FileBasedNodeStorageTree;
import gr.bookapp.storage.file.ObjectTable;
import java.io.IOException;
import java.net.SocketAddress;
import java.time.Instant;

public final class AppServer {

    public static void main(String[] args) throws IOException, ConfigurationFileLoadException {
        DatabaseConfigLoader configLoader = new DatabaseConfigLoader();
        BooklyDatabaseConfig booklyDatabaseConfig = configLoader.get();

        IdGenerator idGenerator = new IdGenerator();

        // Codecs
        StringCodec stringCodec = new StringCodec();
        LongCodec longCodec = new LongCodec();
        InstantCodec instantCodec = new InstantCodec();
        ListCodec<String> stringListCodec = new ListCodec<>(stringCodec);
        BookCodec bookCodec = new BookCodec(stringCodec, stringListCodec, instantCodec);
        BookSalesCodec bookSalesCodec = new BookSalesCodec();
        OfferCodec offerCodec = new OfferCodec(stringListCodec, instantCodec);
        UserCodec userCodec = new UserCodec(stringCodec);
        AuditCodec auditCodec = new AuditCodec(stringCodec, instantCodec);

        // DBs
        // --- Book
        FileBasedNodeStorageTree<Long, Book> bookNodeStorage = new FileBasedNodeStorageTree<>(booklyDatabaseConfig.booksPath(), longCodec, bookCodec);
        ObjectTable<Long, Book> bookObjectTable = new BinarySearchTree<>(Long::compareTo, bookNodeStorage);
        Database<Long, Book> bookDataBase = new Database<>(bookObjectTable);
        // --- BookSales
        FileBasedNodeStorageTree<Long, BookSales> bookSalesNodeStorage = new FileBasedNodeStorageTree<>(booklyDatabaseConfig.bookSalesPath(), longCodec, bookSalesCodec);
        ObjectTable <Long, BookSales> bookSalesObjectTable = new BinarySearchTree<>(Long::compareTo, bookSalesNodeStorage);
        Database<Long, BookSales> bookSalesDataBase = new Database<>(bookSalesObjectTable);
        // --- User
        FileBasedNodeStorageTree<Long, User> userNodeStorage = new FileBasedNodeStorageTree<>(booklyDatabaseConfig.userPath(), longCodec, userCodec);
        ObjectTable <Long, User> userObjectTable = new BinarySearchTree<>(Long::compareTo, userNodeStorage);
        Database<Long, User> userDataBase = new Database<>(userObjectTable);
        // --- Offer
        FileBasedNodeStorageTree<Long, Offer> offerNodeStorage = new FileBasedNodeStorageTree<>(booklyDatabaseConfig.offersPath(), longCodec, offerCodec);
        ObjectTable <Long, Offer> offerObjectTable = new BinarySearchTree<>(Long::compareTo, offerNodeStorage);
        Database<Long, Offer> offerDataBase = new Database<>(offerObjectTable);
        // --- Audit
        FileBasedNodeStorageTree<Instant, Audit> auditNodeStorage = new FileBasedNodeStorageTree<>(booklyDatabaseConfig.auditsPath(), instantCodec, auditCodec);
        ObjectTable <Instant, Audit> auditObjectTable = new BinarySearchTree<>(Instant::compareTo, auditNodeStorage);
        Database<Instant, Audit> auditDataBase = new Database<>(auditObjectTable);

        // Repos
        UserRepository userRepository = new UserRepositoryDbImpl(userDataBase, idGenerator);
        BookRepository bookRepositoryDb = new BookRepositoryDbImpl(bookDataBase, idGenerator);
        BookSalesRepository bookSalesRepositoryDb = new BookSalesRepositoryDbImpl(bookSalesDataBase);
        OfferRepository offerRepositoryDb = new OfferRepositoryDbImpl(offerDataBase, idGenerator);

        // Services
        UserServiceDbImpl userServiceDb = new UserServiceDbImpl(userRepository);
        BookServiceDbImpl bookServiceDb = new BookServiceDbImpl(bookRepositoryDb);
        BookSalesServiceDbImpl bookSalesServiceDb = new BookSalesServiceDbImpl(bookSalesRepositoryDb);
        OfferServiceDbImpl offerServiceDb = new OfferServiceDbImpl(offerRepositoryDb);

        // Server
        SocketAddress address = booklyDatabaseConfig.address();
        Logger.Factory logFactory = new CompositeLoggerFactory(new FileLogger(booklyDatabaseConfig.logsPath()));
        RequestStreamCodec requestStreamCodec = new RequestStreamCodec(bookCodec, bookSalesCodec, offerCodec, stringCodec, stringListCodec, instantCodec);
        Controller controller = new Controller(userServiceDb, bookServiceDb, bookSalesServiceDb, offerServiceDb);
        TCPServer server = new TCPServer(address, logFactory, controller, requestStreamCodec);

        server.run();
    }

}
