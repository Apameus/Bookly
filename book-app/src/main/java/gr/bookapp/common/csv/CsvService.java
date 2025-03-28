package gr.bookapp.common.csv;

import gr.bookapp.exceptions.ConfigurationFileLoadException;
import gr.bookapp.exceptions.CsvFileLoadException;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.Book;
import gr.bookapp.models.BookSales;
import gr.bookapp.models.Offer;
import gr.bookapp.models.User;
import gr.bookapp.repositories.BookSalesRepository;
import gr.bookapp.repositories.OfferRepository;
import gr.bookapp.repositories.UserRepository;
import gr.bookapp.services.BookService;

import java.nio.file.Path;
import java.util.List;

public final class CsvService {
    private final CsvLoader csvLoader;
    private final BookService bookService;
    private final BookSalesRepository bookSalesRepository;
    private final UserRepository userRepository;
    private final OfferRepository offerRepository;
    private final CsvParser<Book> bookCsvParser;
    private final CsvParser<BookSales> bookSalesCsvParser;
    private final CsvParser<User> userCsvParser;
    private final CsvParser<Offer> offerCsvParser;

    public CsvService(CsvLoader csvLoader, BookService bookService, BookSalesRepository bookSalesRepository, UserRepository userRepository, OfferRepository offerRepository) {
        this.csvLoader = csvLoader;
        this.bookService = bookService;
        this.bookSalesRepository = bookSalesRepository;
        this.userRepository = userRepository;
        this.offerRepository = offerRepository;
        bookCsvParser = new BookCsvParser();
        bookSalesCsvParser = new BookSalesCsvParser();
        userCsvParser = new UserCsvParser();
        offerCsvParser = new OfferCsvParser();
    }

    public void updateBooks(String pathToCsv) throws CsvFileLoadException {
        List<Book> books = csvLoader.load(Path.of(pathToCsv), bookCsvParser);
        books.forEach(bookService::addBook);
    }

    public void updateBookSales(String pathToCsv) throws CsvFileLoadException {
        List<BookSales> bookSales = csvLoader.load(Path.of(pathToCsv), bookSalesCsvParser);
        bookSales.forEach(bookSalesRepository::add);
    }

    public void updateUsers(String pathToCsv) throws InvalidInputException, CsvFileLoadException {
        List<User> users = csvLoader.load(Path.of(pathToCsv), userCsvParser);
        for (User user : users) userRepository.add(user);
    }

    public void updateOffers(String pathToCsv) throws CsvFileLoadException {
        List<Offer> offers = csvLoader.load(Path.of(pathToCsv), offerCsvParser);
        offers.forEach(offerRepository::add);
    }
}
