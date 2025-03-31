package gr.bookapp.common.csv;

import gr.bookapp.exceptions.CsvFileLoadException;
import gr.bookapp.models.BookSales;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookSalesCsvParserTest {
    CsvParser<BookSales> bookSalesCsvParser = new BookSalesCsvParser();

    @Test
    @DisplayName("Parse bookSales test")
    void parseBookSalesTest() throws CsvFileLoadException {
        BookSales aSales = new BookSales(1111, 10);
        BookSales bSales = new BookSales(5454, 12);
        BookSales cSales = new BookSales(1234, 6);
        String csv = """
                1111,10
                5454,12
                1234,6
                """;
        String[] lines = csv.split("\n");

        assertThat(bookSalesCsvParser.parse(lines[0])).isEqualTo(aSales);
        assertThat(bookSalesCsvParser.parse(lines[1])).isEqualTo(bSales);
        assertThat(bookSalesCsvParser.parse(lines[2])).isEqualTo(cSales);
    }
}