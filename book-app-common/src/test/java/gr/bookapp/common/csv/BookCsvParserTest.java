package gr.bookapp.common.csv;

import gr.bookapp.common.InstantFormatter;
import gr.bookapp.exceptions.CsvFileLoadException;
import gr.bookapp.models.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class BookCsvParserTest {
    CsvParser<Book> bookCsvParser = new BookCsvParser();


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

        assertThat(bookCsvParser.parse(lines[0])).isEqualTo(odyssey);
        assertThat(bookCsvParser.parse(lines[1])).isEqualTo(iliada);
        assertThat(bookCsvParser.parse(lines[2])).isEqualTo(doom);
    }

}