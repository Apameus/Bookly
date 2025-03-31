package gr.bookapp.csv;

import gr.bookapp.exceptions.CsvFileLoadException;

public interface CsvParser<T> {
    public T parse(String line) throws CsvFileLoadException;
}
