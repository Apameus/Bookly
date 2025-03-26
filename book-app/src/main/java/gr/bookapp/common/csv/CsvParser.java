package gr.bookapp.common.csv;

import gr.bookapp.common.InstantFormatter;
import gr.bookapp.exceptions.CsvFileLoadException;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.*;
import gr.bookapp.repositories.BookSalesRepository;
import gr.bookapp.repositories.UserRepository;
import gr.bookapp.repositories.OfferRepository;
import gr.bookapp.services.BookService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public interface CsvParser<T> {
    public T parse(String line) throws CsvFileLoadException;
}
