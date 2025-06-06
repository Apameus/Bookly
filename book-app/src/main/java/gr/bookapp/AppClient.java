package gr.bookapp;

import gr.bookapp.client.Client;
import gr.bookapp.client.ClientImpl;
import gr.bookapp.config.BooklyClientConfig;
import gr.bookapp.config.ClientConfigLoader;
import gr.bookapp.csv.CsvLoader;
import gr.bookapp.protocol.codec.*;
import gr.bookapp.protocol.packages.RequestStreamCodec;
import gr.bookapp.protocol.packages.ResponseStreamCodec;
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
import java.net.SocketAddress;
import java.time.Clock;

public final class AppClient {


    public static void main(String[] args) throws IOException, ConfigurationFileLoadException {
        ClientConfigLoader configLoader = new ClientConfigLoader();
        BooklyClientConfig booklyConfig = configLoader.get();

        Logger.Factory loggerFactory = new CompositeLoggerFactory(new ConsoleLogger(), new FileLogger(booklyConfig.logsPath()));

        StringCodec stringCodec = new StringCodec();
        ListCodec<String> listCodec = new ListCodec<>(stringCodec);
        InstantCodec instantCodec = new InstantCodec();
        BookCodec bookCodec = new BookCodec(stringCodec, listCodec, instantCodec);
        BookSalesCodec bookSalesCodec = new BookSalesCodec();
        OfferCodec offerCodec = new OfferCodec(listCodec, instantCodec);

        SocketAddress address = booklyConfig.address();

        RequestStreamCodec requestStreamCodec = new RequestStreamCodec(bookCodec, bookSalesCodec, offerCodec, stringCodec, listCodec, instantCodec);
        ResponseStreamCodec responseStreamCodec = new ResponseStreamCodec();

        Client client = new ClientImpl(requestStreamCodec, responseStreamCodec, address);

        //Repos
        BookRepository bookRepository = new BookRepositoryClientImpl(client);
        BookSalesRepository bookSalesRepository = new BookSalesRepositoryClientImpl(client);
        UserRepository userRepository = new UserRepositoryClientImpl(client);
        OfferRepository offerRepository = new OfferRepositoryClientImpl(client);

        //Services
        BookSalesService bookSalesService = new BookSalesService(bookSalesRepository, loggerFactory);
        BookService bookService = new BookService(bookRepository, loggerFactory);
        OfferService offerService = new OfferService(offerRepository, Clock.systemUTC(), loggerFactory);
        UserService userService = new UserService(userRepository, bookRepository, offerService, bookSalesService, loggerFactory);
        AdminService adminService = new AdminService(userRepository, loggerFactory);
        AuthenticationService authenticationService = new AuthenticationService(userRepository, loggerFactory);

        CsvService csvService = new CsvService(new CsvLoader(), bookService, bookSalesRepository, userRepository, offerRepository);

        //UI
        LoginPanelUI loginPanelUI = new LoginPanelUI(authenticationService, userService, userRepository);
        AdminPanelUI adminPanelUI = new AdminPanelUI(csvService, adminService);
        EmployeePanel employeePanel = new EmployeePanel(userService, bookService);
        TerminalUI terminalUI = new TerminalUI(loginPanelUI, adminPanelUI, employeePanel);
        terminalUI.start();
    }
}
