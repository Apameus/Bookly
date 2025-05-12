package server;

import gr.bookapp.client.Client;
import gr.bookapp.client.ClientImpl;
import gr.bookapp.common.IdGenerator;
import gr.bookapp.database.Controller;
import gr.bookapp.database.TCPServer;
import gr.bookapp.log.CompositeLoggerFactory;
import gr.bookapp.log.ConsoleLogger;
import gr.bookapp.log.Logger;
import gr.bookapp.protocol.codec.*;
import gr.bookapp.protocol.packages.RequestStreamCodec;
import gr.bookapp.protocol.packages.ResponseStreamCodec;
import gr.bookapp.repositories.BookRepository;
import gr.bookapp.repositories.BookSalesRepository;
import gr.bookapp.repositories.OfferRepository;
import gr.bookapp.repositories.UserRepository;
import gr.bookapp.services.BookSalesServiceDbImpl;
import gr.bookapp.services.BookServiceDbImpl;
import gr.bookapp.services.OfferServiceDbImpl;
import gr.bookapp.services.UserServiceDbImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public abstract class ServerAbstractIntegrationTest {

    static Client client;

    static UserRepository userRepository = Mockito.mock(UserRepository.class);
    static BookRepository bookRepository = Mockito.mock(BookRepository.class);
    static BookSalesRepository bookSalesRepository = Mockito.mock(BookSalesRepository.class);
    static OfferRepository offerRepository = Mockito.mock(OfferRepository.class);

    static UserServiceDbImpl userServiceDb;
    static BookServiceDbImpl bookServiceDb;
    static BookSalesServiceDbImpl bookSalesServiceDb;
    static OfferServiceDbImpl offerServiceDb;

    static TCPServer tcpServer;
    static Controller controller;
    static RequestStreamCodec requestStreamCodec;
    static SocketAddress address;
    static Logger.Factory logFactory;

    static StringCodec stringCodec;
    static InstantCodec instantCodec;
    static ListCodec<String> stringListCodec;
    static BookCodec bookCodec;
    static BookSalesCodec bookSalesCodec;
    static OfferCodec offerCodec;


    @BeforeAll
    static void setup() throws IOException {
        // Services
        userServiceDb = new UserServiceDbImpl(userRepository);
        bookServiceDb = new BookServiceDbImpl(bookRepository);
        bookSalesServiceDb = new BookSalesServiceDbImpl(bookSalesRepository);
        offerServiceDb = new OfferServiceDbImpl(offerRepository);

        // Codecs
        stringCodec = new StringCodec();
        instantCodec = new InstantCodec();
        stringListCodec = new ListCodec<>(stringCodec);
        bookCodec = new BookCodec(stringCodec, stringListCodec, instantCodec);
        bookSalesCodec = new BookSalesCodec();
        offerCodec = new OfferCodec(stringListCodec, instantCodec);

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

    @AfterAll
    static void stopServer() throws Exception {
        tcpServer.close();
    }}
