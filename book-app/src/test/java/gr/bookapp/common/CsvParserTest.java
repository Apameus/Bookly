package gr.bookapp.common;

import gr.bookapp.exceptions.CsvFileLoadException;
import gr.bookapp.models.Book;
import gr.bookapp.models.BookSales;
import gr.bookapp.repositories.BookSalesRepository;
import gr.bookapp.repositories.UserRepository;
import gr.bookapp.repositories.OfferRepository;
import gr.bookapp.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.List;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CsvParserTest {
    BookService bookService = Mockito.mock(BookService.class);
    BookSalesRepository bookSalesRepository = Mockito.mock(BookSalesRepository.class);
    UserRepository employeeRepository = Mockito.mock(UserRepository.class);
    OfferRepository offerRepository = Mockito.mock(OfferRepository.class);
    CsvParser csvParser;

    @BeforeEach
    void setup(){
        csvParser = new CsvParser(bookService, bookSalesRepository, employeeRepository, offerRepository);
    }

    @Test
    @DisplayName("Parse books test")
    void parseBooksTest() throws CsvFileLoadException {
        Book odyssey = new Book(1111,"Odyssey",List.of("Omiros"), 100, InstantFormatter.parse("01-01-0800 BC"), List.of("Philosophy", "Adventure"));
        Book iliada = new Book(5454,"Iliada", List.of("Omiros"), 120, InstantFormatter.parse("01-02-0800 BC"), List.of("Philosophy", "Adventure"));
        Book doom = new Book(1234, "Doom", List.of("Nightmare", "Darkness"), 666, InstantFormatter.parse("06-06-0666 BC"), List.of("Adventure"));
        String csv = """
                1111,Odyssey,1,Omiros,100,01-01-0800 BC,2,Philosophy,Adventure
                5454,Iliada,1,Omiros,120,01-02-0800 BC,2,Philosophy,Adventure
                1234,Doom,2,Nightmare,Darkness,666,06-06-0666 BC,1,Adventure
                """;
        String[] lines = csv.split("\n");
        csvParser.updateBooks(List.of(lines));

        verify(bookService, times(1)).addBook(odyssey);
        verify(bookService, times(1)).addBook(iliada);
        verify(bookService, times(1)).addBook(doom);
    }

    @Test
    @DisplayName("Parse bookSales test")
    void parseBookSalesTest() {
        BookSales odysseySales = new BookSales(1111, 22);
        BookSales iliadaSales = new BookSales(5454, 0);
        BookSales bookNonExistingInDbSales = new BookSales(999999, 43);
    }
}