package gr.bookapp.ui;

import gr.bookapp.common.IdGenerator;
import gr.bookapp.exceptions.AuthenticationFailedException;
import gr.bookapp.models.Book;
import gr.bookapp.models.Employee;
import gr.bookapp.models.Offer;
import gr.bookapp.repositories.BookRepository;
import gr.bookapp.services.AuthenticationService;
import gr.bookapp.services.BookService;
import gr.bookapp.services.EmployeeService;

import java.io.Console;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public final class TerminalUI {
    private final Console console = System.console();
    private final IdGenerator idGenerator;
    private final AuthenticationService authenticationService;
    private final EmployeeService employeeService;
    private final BookService bookService;

    public TerminalUI(IdGenerator idGenerator, AuthenticationService authenticationService, EmployeeService employeeService, BookService bookService) {
        this.idGenerator = idGenerator;
        this.authenticationService = authenticationService;
        this.employeeService = employeeService;
        this.bookService = bookService;
    }

    public void start(){
        initialLoop:
        while (true){
            String username = console.readLine("Username: ");
            String password = console.readLine("Password: ");
            try {
                Employee employee = authenticationService.authenticate(username, password);
            } catch (AuthenticationFailedException e) {
                System.err.println("Authentication failed! Please try again.");
                continue initialLoop;
            }
            employeeLoop:
            while (true) {
                String action = console.readLine("Search_Book, Add_Book, Delete_Book, Create_Offer, Exit");
                switch (action.toLowerCase()) {
                    case "search_book" -> searchBook();
                    case "add_book" -> addBook();
                    case "delete_book" -> deleteBook();
                    case "create_offer" -> createOffer();
                    case "exit" -> { break employeeLoop; }
                }
            }
        }
    }

    private void createOffer() {
        String inputTags = console.readLine("Tags (separated by space): ");
        List<String> tags = Arrays.stream(inputTags.split(" ")).toList();
        int percentage = Integer.parseInt(console.readLine("Percentage: "));
        Instant untilDate = LocalDate.parse(console.readLine("Until_Date (MM-yyyy): ")).atStartOfDay().toInstant(ZoneOffset.UTC);

        long id = idGenerator.generateID();
        Offer offer = new Offer(id, tags, percentage, untilDate);
    }

    private void deleteBook() {
        try {
            long bookID = Long.parseLong(console.readLine("Book_ID: "));
            bookService.deleteBookByID(bookID);
        }catch (Exception e) {
            System.err.println("Invalid input !");
        }

    }

    private void addBook() { //todo should add try catch
        String name = console.readLine("Book_Name:");
        String inputAuthors = console.readLine("Authors (separated by space): ");
        List<String> authors = Arrays.stream(inputAuthors.split(" ")).toList();
        double price = Double.parseDouble(console.readLine("Price: "));
        String inputDate = console.readLine("Release_Date (MM-yyyy): ");
        Instant releaseDate = LocalDate.parse(inputDate).atStartOfDay().toInstant(ZoneOffset.UTC);
        String inputTags = console.readLine("Tags (separated by space): ");
        List<String> tags = Arrays.stream(inputTags.split(" ")).toList();

        long id = idGenerator.generateID();
        Book book = new Book(id, name, authors, price, releaseDate, tags);

        bookService.addBook(book);
    }

    private void searchBook() {
        String by = console.readLine("By: Name, Authors, Tags, Price_Range, Release_Date_Range");
        switch (by.toLowerCase()){
            case "name" -> searchBookByName(console.readLine("Name: "));
            case "author" -> searchBookByAuthors(console.readLine("Authors: "));
            case "tags" -> searchBookByTags(console.readLine("Tags: "));
            case "price_range" -> searchBookByPrice();
            case "release_date_range" -> searchBookByDate();
        }

    }

    private void searchBookByDate() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
            LocalDate minLocalDate = LocalDate.parse(console.readLine("Min date (mm yyyy): "));
            LocalDate maxLocalDate = LocalDate.parse(console.readLine("Max date (mm yyyy): "));
            bookService.getBooksInDateRange(minLocalDate.atStartOfDay().toInstant(ZoneOffset.UTC), maxLocalDate.atStartOfDay().toInstant(ZoneOffset.UTC));
        }catch (Exception e){
            System.err.println("Invalid Input !");
        }

    }

    private void searchBookByPrice() {
        try {
            double minPrice = Double.parseDouble(console.readLine("Min price: "));
            double maxPrice = Double.parseDouble(console.readLine("Max price: "));
            bookService.getBooksInPriceRange(minPrice, maxPrice).forEach(book -> console.printf("id: %s, Name: %s, Authors: %s, Price: %s, Release_Date, Tags: %s").format(String.valueOf(book.id()), book.name(), book.authors(), book.price(), book.releaseDate(), book.tags()));
        } catch (Exception e) {
            System.err.println("Invalid Input !");
        }
    }

    private void searchBookByTags(String input) {
        String[] tags = input.split(" ");
        bookService.getBooksByTags(Arrays.stream(tags).toList()).forEach(book -> console.printf("id: %s, Name: %s, Authors: %s, Price: %s, Release_Date, Tags: %s").format(String.valueOf(book.id()), book.name(), book.authors(), book.price(), book.releaseDate(), book.tags()));
    }

    private void searchBookByAuthors(String input) {
        String[] authors = input.split(" ");
        bookService.getBooksByAuthors(Arrays.stream(authors).toList()).forEach(book -> console.printf("id: %s, Name: %s, Authors: %s, Price: %s, Release_Date, Tags: %s").format(String.valueOf(book.id()), book.name(), book.authors(), book.price(), book.releaseDate(), book.tags()));
    }

    private void searchBookByName(String name) {
        bookService.getBooksByName(name).forEach(book -> console.printf("id: %s, Name: %s, Authors: %s, Price: %s, Release_Date, Tags: %s").format(String.valueOf(book.id()), book.name(), book.authors(), book.price(), book.releaseDate(), book.tags()));
    }
}
