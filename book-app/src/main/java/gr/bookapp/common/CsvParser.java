package gr.bookapp.common;

import gr.bookapp.exceptions.CsvFileLoadException;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.*;
import gr.bookapp.repositories.BookSalesRepository;
import gr.bookapp.repositories.EmployeeRepository;
import gr.bookapp.repositories.OfferRepository;
import gr.bookapp.services.BookService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class CsvParser {
    private final BookService bookService;
    private final BookSalesRepository bookSalesRepository;
    private final EmployeeRepository employeeRepository;
    private final OfferRepository offerRepository;

    public CsvParser(BookService bookService, BookSalesRepository bookSalesRepository, EmployeeRepository employeeRepository, OfferRepository offerRepository) {
        this.bookService = bookService;
        this.bookSalesRepository = bookSalesRepository;
        this.employeeRepository = employeeRepository;
        this.offerRepository = offerRepository;
    }

    /**
     * @param lines lines from csv file
     * @throws CsvFileLoadException if the specified lines aren't compatible
     */
    public void updateBooks(List<String> lines) throws CsvFileLoadException {
        for (String line : lines) {
            Book book = parseBook(line);
            bookService.addBook(book);
        }
    }


    /**
     * @param lines lines from csv file
     * @throws CsvFileLoadException if the specified lines aren't compatible
     */
    public void updateBookSales(List<String> lines) throws CsvFileLoadException {
        //TODO: -What if the bookID doesn't exist? -What if the bookID existing and the sales are fewer (should we combine)?
        for (String line : lines) {
            BookSales bookSales = parseBookSales(line);
            bookSalesRepository.add(bookSales);
        }
    }



    /**
     * @param lines lines from csv file
     * @throws CsvFileLoadException if the specified lines aren't compatible
     */
    public void updateEmployees(List<String> lines) throws InvalidInputException, CsvFileLoadException {
        for (String line : lines) {
            Employee employee = parseEmployee(line);
            employeeRepository.add(employee);
        }
    }


    /**
     * @param lines lines from csv file
     * @throws CsvFileLoadException if the specified lines aren't compatible
     */
    public void updateOffers(List<String> lines) throws CsvFileLoadException {
        for (String line : lines) {
            Offer offer = parseOffer(line);
            offerRepository.add(offer);
        }
    }

//    public void updateAll(String booklyDataCSV){
//
//    }

    //id,name,authorsSize,author1,author2,price,tagSize,tag1,tag2
    //007,babis,3,a1,a2,a3,100,01-01-2002 AD,2,b1,b2
    private Book parseBook(String line) throws CsvFileLoadException {
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

    private static BookSales parseBookSales(String line) throws CsvFileLoadException {
        try {
            String[] values = line.split(",");
            long bookID = Long.parseLong(values[0]);
            long sales = Long.parseLong(values[1]);
            return new BookSales(bookID, sales);
        } catch (Exception e) { throw new CsvFileLoadException("BookSales.csv is incompatible"); }
    }

    private Employee parseEmployee(String line) throws CsvFileLoadException {
        try {
            String[] values = line.split(",");
            long id = Long.parseLong(values[0]);
            String username = values[1];
            String password = values[2];
            Role role = Role.valueOf(values[3]);
            return new Employee(id, username, password, role);
        } catch (Exception e) {
            throw new CsvFileLoadException("Employees.csv is incompatible !");
        }
    }

    private Offer parseOffer(String line) throws CsvFileLoadException {
        try {
            String[] values = line.split(",");
            long offerID = Long.parseLong(values[0]);
            List<String> tags = parseList(values, 1);
            int percentage = Integer.parseInt(values[2 + tags.size()]);
            Instant untilDate = InstantFormatter.parse(values[3 + tags.size()]);
            return new Offer(offerID, tags, percentage, untilDate);
        } catch (Exception e) {
            throw new CsvFileLoadException("Offers.csv is incompatible !");
        }
    }

    private List<String> parseList(String[] inputValues, int index) {
        int listSize = Integer.parseInt(inputValues[index]);
        List<String> list = new ArrayList<>(listSize);
        for (int i = 1; i <= listSize; i++) list.add(inputValues[i + index]);
        return list;
    }


}
