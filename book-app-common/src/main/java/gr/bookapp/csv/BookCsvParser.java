package gr.bookapp.csv;

import gr.bookapp.common.InstantFormatter;
import gr.bookapp.exceptions.CsvFileLoadException;
import gr.bookapp.models.Book;
import java.util.ArrayList;
import java.time.Instant;
import java.util.List;

public record BookCsvParser() implements CsvParser<Book> {


    //id,name,authorsSize,author1,author2,price,tagSize,tag1,tag2
    //007,babis,3,a1,a2,a3,100,01-01-2002 AD,2,b1,b2
    @Override
    public Book parse(String line) throws CsvFileLoadException {
        try {
            String[] values = line.split(",");
            long id = Long.parseLong(values[0]);
            String name = values[1];
            List<String> authors = parseList(values, 2);
            double price = Double.parseDouble(values[3 + authors.size()]);
            Instant releaseDate = InstantFormatter.parse(values[4 + authors.size()]);
            List<String> tags = parseList(values, 5 + authors.size());
            return new Book(id, name, authors, price, releaseDate, tags);
        } catch (Exception e) {throw new CsvFileLoadException(e.getMessage());}
    }


    private List<String> parseList(String[] inputValues, int index) {
        int listSize = Integer.parseInt(inputValues[index]);
        List<String> list = new ArrayList<>(listSize);
        for (int i = 1; i <= listSize; i++) list.add(inputValues[i + index]);
        return list;
    }

}
