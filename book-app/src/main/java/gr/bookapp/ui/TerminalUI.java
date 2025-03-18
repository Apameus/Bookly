package gr.bookapp.ui;

import gr.bookapp.common.AuditContextImpl;
import gr.bookapp.common.IdGenerator;
import gr.bookapp.exceptions.AuthenticationFailedException;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.Book;
import gr.bookapp.models.Employee;
import gr.bookapp.models.Offer;
import gr.bookapp.services.AuthenticationService;
import gr.bookapp.services.BookService;
import gr.bookapp.services.EmployeeService;
import gr.bookapp.storage.codec.InstantFormatter;

import java.io.Console;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public final class TerminalUI {
    private final Console console ;
    private final IdGenerator idGenerator;
    private final AuthenticationService authenticationService;
    private final EmployeeService employeeService;
    private final BookService bookService;

    public TerminalUI(IdGenerator idGenerator, AuthenticationService authenticationService, EmployeeService employeeService, BookService bookService) {
        this.idGenerator = idGenerator;
        this.authenticationService = authenticationService;
        this.employeeService = employeeService;
        this.bookService = bookService;
        this.console = System.console();
    }

    public void start(){
//        setupLoop:
//        while (true){
//            String input = console.readLine("Create Employees (Un Ps): ");
//            if (input.equalsIgnoreCase("exit")) break ;
//            String[] ss = input.split(" ");
//            employeeService.hireEmployee(ss[0], ss[1]);
//        }
        initialLoop:
        while (true){
            String username = console.readLine("Username: ");
            String password = console.readLine("Password: ");
            try {
                Employee employee = authenticationService.authenticate(username, password);
                AuditContextImpl.add(employee.id());
            } catch (AuthenticationFailedException e) {
                System.err.println("Authentication failed! Please try again.");
                continue initialLoop;
            }
            employeeLoop:
            while (true) {
                String action = console.readLine("Sell_Book, Search_Book, Add_Book, Delete_Book, Create_Offer, Exit: ");
                switch (action.toLowerCase()) {
                    case "sell_book" -> sellBook();
                    case "search_book" -> searchBook();
                    case "add_book" -> addBook();
                    case "delete_book" -> deleteBook();
                    case "create_offer" -> createOffer();
                    case "exit" -> { break employeeLoop; }
                    default -> System.err.println("Invalid Input !");
                }
            }
        }
    }

    private void sellBook() {
        Long bookID = null;
        String input = console.readLine("Book_ID: ");
        try {
            bookID = Long.parseLong(input);
        } catch (Exception e) {
            System.err.println("Invalid input !");
            return;
        }
        try {
            Book book = employeeService.sellBook(bookID);
            printBookDetails(book);

        } catch (InvalidInputException e) {
            System.err.println("Invalid BookID");
        }
    }

    private void createOffer() {
        try {
            String inputTags = console.readLine("Tags (separated by space): ");
            List<String> tags = Arrays.stream(inputTags.split(" ")).toList();
            int percentage = Integer.parseInt(console.readLine("Percentage: "));
            Long inputDuration = Long.valueOf(console.readLine("Duration in days: "));
            Duration duration = Duration.ofDays(inputDuration);
            long id = idGenerator.generateID();
            employeeService.createOffer(tags, percentage, duration);
        } catch (Exception e) {
            System.err.println("Invalid input !");
        }
//        String inputDate = console.readLine("Until Date (dd-MM-yyyy G): ");
//        Instant untilDate = InstantFormatter.parse(inputDate);
//        Offer offer = new Offer(id, tags, percentage, untilDate);
    }

    private void deleteBook() {
        try {
            long bookID = Long.parseLong(console.readLine("Book_ID: "));
            bookService.deleteBookByID(bookID);
        }catch (Exception e) {
            System.err.println("Invalid input !");
        }

    }

    private void addBook() {
        Book book = null;
        try {
            String name = console.readLine("Book_Name: ");
            String inputAuthors = console.readLine("Authors (separated by space): ");
            List<String> authors = Arrays.stream(inputAuthors.split(" ")).toList();
            double price = Double.parseDouble(console.readLine("Price: "));
            String inputDate = console.readLine("Release_Date (dd-MM-yyyy G): "); //22-02-0300 BC
            Instant releaseDate = InstantFormatter.parse(inputDate);
            String inputTags = console.readLine("Tags (separated by space): ");
            List<String> tags = Arrays.stream(inputTags.split(" ")).toList();

            long id = idGenerator.generateID();
            book = new Book(id, name, authors, price, releaseDate, tags);

        }catch (Exception e) {
            System.err.println("Invalid input !");
        }
        bookService.addBook(book);
    }

    private void searchBook() {
        String by = console.readLine("By Name, Authors, Tags, Price_Range, Release_Date_Range: ");
        switch (by.toLowerCase()){
            case "name" -> searchBookByName(console.readLine("Name: "));
            case "authors" -> searchBookByAuthors(console.readLine("Authors: "));
            case "tags" -> searchBookByTags(console.readLine("Tags: "));
            case "price_range" -> searchBookByPrice();
            case "release_date_range" -> searchBookByDate();
        }

    }

    private void searchBookByDate() {
        try {
            String min = console.readLine("Min date (dd-MM-yyyy G): ");
            String max = console.readLine("Max date (dd-MM-yyyy G): ");
            bookService.getBooksInDateRange(InstantFormatter.parse(min), InstantFormatter.parse(max)).forEach(this::printBookDetails);
        }catch (Exception e){
            System.err.println("Invalid Input !");
        }

    }

    private void searchBookByPrice() {
        try {
            double minPrice = Double.parseDouble(console.readLine("Min price: "));
            double maxPrice = Double.parseDouble(console.readLine("Max price: "));
            bookService.getBooksInPriceRange(minPrice, maxPrice).forEach(this::printBookDetails);
        } catch (Exception e) {
            System.err.println("Invalid Input !");
        }
    }

    private void searchBookByTags(String input) {
        String[] tags = input.split(" ");
        bookService.getBooksByTags(Arrays.stream(tags).toList()).forEach(this::printBookDetails);
    }

    private void searchBookByAuthors(String input) {
        String[] authors = input.split(" ");
        bookService.getBooksByAuthors(Arrays.stream(authors).toList()).forEach(this::printBookDetails);
    }

    private void searchBookByName(String name) {
        bookService.getBooksByName(name).forEach(this::printBookDetails);
    }

    private void printBookDetails(Book book) {
        console.printf("id: %s, Name: %s, Authors: %s, Price: %s, Release_Date: %s, Tags: %s", book.id(), book.name(), book.authors(), book.price(), book.releaseDate(), book.tags());
    }
}
