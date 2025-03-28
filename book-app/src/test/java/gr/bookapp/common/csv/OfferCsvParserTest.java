package gr.bookapp.common.csv;

import gr.bookapp.common.InstantFormatter;
import gr.bookapp.exceptions.CsvFileLoadException;
import gr.bookapp.models.Offer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OfferCsvParserTest {
    CsvParser<Offer> bookCsvParser = new OfferCsvParser();


    @Test
    @DisplayName("Parse offers test")
    void parseOffersTest() throws CsvFileLoadException {
        Offer offerA = new Offer(11, List.of("Philosophy"), 20, InstantFormatter.parse("20-06-2025 AD"));
        Offer offerB = new Offer(22, List.of("Adventure"), 10, InstantFormatter.parse("26-04-2025 AD"));
        Offer offerC = new Offer(33, List.of("Philosophy", "Adventure", "Politics"), 5, InstantFormatter.parse("30-04-2025 AD"));
        String csv = """
                11,1,Philosophy,20,20-06-2025 AD
                22,1,Adventure,10,26-04-2025 AD
                33,3,Philosophy,Adventure,Politics,5,30-04-2025 AD
                """;
        String[] lines = csv.split("\n");

        assertThat(bookCsvParser.parse(lines[0])).isEqualTo(offerA);
        assertThat(bookCsvParser.parse(lines[1])).isEqualTo(offerB);
        assertThat(bookCsvParser.parse(lines[2])).isEqualTo(offerC);
    }
}