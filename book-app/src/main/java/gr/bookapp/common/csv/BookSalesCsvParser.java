package gr.bookapp.common.csv;

import gr.bookapp.exceptions.CsvFileLoadException;
import gr.bookapp.models.BookSales;

public record BookSalesCsvParser() implements CsvParser<BookSales> {

    @Override
    public BookSales parse(String line) throws CsvFileLoadException {
        //TODO: -What if the bookID doesn't exist? -What if the bookID existing and the sales are fewer (should we combine)?
        try {
            String[] values = line.split(",");
            long bookID = Long.parseLong(values[0]);
            long sales = Long.parseLong(values[1]);
            return new BookSales(bookID, sales);
        } catch (Exception e) { throw new CsvFileLoadException("BookSales.csv is incompatible"); }
    }
}
