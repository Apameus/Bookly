package gr.bookapp.ui;

import gr.bookapp.common.AuditContextImpl;
import gr.bookapp.common.IdGenerator;
import gr.bookapp.common.InstantFormatter;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.Book;
import gr.bookapp.services.BookService;
import gr.bookapp.services.UserService;

import java.io.Console;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public final class EmployeePanel {
    private final Console console = System.console();
    private final UserService userService;
    private final BookService bookService;
    private final boolean EXIT_COMMAND = false;

    public EmployeePanel(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
    }

    public boolean handleEmployeeActions() {
        String action = console.readLine("Sell_Book, Search_Book, Add_Book, Delete_Book, Create_Offer, Exit: ");
        switch (action.toLowerCase()) {
            case "sell_book" -> sellBook();
            case "search_book" -> searchBook();
            case "add_book" -> addBook();
            case "delete_book" -> deleteBook();
            case "create_offer" -> createOffer();
            case "exit" -> {
                AuditContextImpl.clear();
                return EXIT_COMMAND;}
            default -> System.err.println("Invalid Input !");
        }
        return true;
    }

    private void sellBook() {
        Long bookID = null;
        String input = console.readLine("Book_ID: ");
        try {
            bookID = Long.parseLong(input);
            Book book = userService.sellBook(bookID);
            printBookDetails(book);
        } catch (NumberFormatException e) {
            System.err.println("Invalid input !");
        } catch (InvalidInputException e) {
            System.err.println("Invalid BookID !");
        }
    }

    private void createOffer() {
        try {
            String inputTags = console.readLine("Tags (separated by space): ");
            if (inputTags.isBlank()) throw new InvalidInputException("Every offer needs at least 1 tag !");
            List<String> tags = Arrays.stream(inputTags.split(" ")).toList();
            int percentage = Integer.parseInt(console.readLine("Percentage: "));
            Long inputDuration = Long.valueOf(console.readLine("Duration in days: "));
            Duration duration = Duration.ofDays(inputDuration);
            userService.createOffer(tags, percentage, duration);
        } catch (Exception e) {
            System.err.println("Invalid input:  " + e.getMessage());
        }
    }

    private void addBook() {
        Book book = null;
        try {
            String name = console.readLine("Book_Name: ");
            if (name.isBlank()) throw new InvalidInputException("Book name can't be empty !");
            String inputAuthors = console.readLine("Authors (separated by space): ");
            if (inputAuthors.isBlank()) throw new InvalidInputException("Every book needs at least 1 author !");
            List<String> authors = Arrays.stream(inputAuthors.split(" ")).toList();
            double price = Double.parseDouble(console.readLine("Price: "));
            String inputDate = console.readLine("Release_Date (dd-MM-yyyy G): "); //22-02-0300 BC
            Instant releaseDate = InstantFormatter.parse(inputDate);
            String inputTags = console.readLine("Tags (separated by space): ");
            if (inputTags.isBlank()) throw new InvalidInputException("Every book needs at least 1 tag !");
            List<String> tags = Arrays.stream(inputTags.split(" ")).toList();

            book = new Book(name, authors, price, releaseDate, tags);

            bookService.addBook(book);
        }catch (DateTimeParseException e) {
            System.err.println("Invalid Date !");
        }catch (NumberFormatException e) {
            System.err.println("Invalid input !");
        }catch (InvalidInputException e){
            System.err.println(e.getMessage());
        }
    }

    private void deleteBook() {
        try {
            long bookID = Long.parseLong(console.readLine("Book_ID: "));
            bookService.deleteBookByID(bookID);
        }catch (NumberFormatException e) {
            System.err.println("Invalid input !");
        }

    }

    private void searchBook() {
        String by = console.readLine("By Name, Authors, Tags, Price_Range, Release_Date_Range, See_All: ");
        switch (by.toLowerCase()){
            case "name" -> searchBookByName(console.readLine("Name: "));
            case "authors" -> searchBookByAuthors(console.readLine("Authors: "));
            case "tags" -> searchBookByTags(console.readLine("Tags: "));
            case "price_range" -> searchBookByPrice();
            case "release_date_range" -> searchBookByDate();
            case "see_all" -> seeAllBooks();
            default -> System.err.println("Invalid input !");
        }

    }

    private void seeAllBooks() {
        bookService.getAllBooks().forEach(this::printBookDetails);
    }

    private void searchBookByDate() {
        try {
            String min = console.readLine("Min date (dd-MM-yyyy G): ");
            String max = console.readLine("Max date (dd-MM-yyyy G): ");
            bookService.getBooksInDateRange(InstantFormatter.parse(min), InstantFormatter.parse(max)).forEach(this::printBookDetails);
        }catch (DateTimeParseException e){
            System.err.println("Invalid date !");
        }

    }

    private void searchBookByPrice() {
        try {
            double minPrice = Double.parseDouble(console.readLine("Min price: "));
            double maxPrice = Double.parseDouble(console.readLine("Max price: "));
            bookService.getBooksInPriceRange(minPrice, maxPrice).forEach(this::printBookDetails);
        } catch (NumberFormatException e) {
            System.err.println("Invalid Input !");
        }
    }

    private void searchBookByTags(String input) {
        String[] tags = input.split(" ");
        bookService.getBooksByTags(Arrays.stream(tags).map(String::toLowerCase).toList()).forEach(this::printBookDetails);
    }

    private void searchBookByAuthors(String input) {
        String[] authors = input.split(" ");
        bookService.getBooksByAuthors(Arrays.stream(authors).map(String::toLowerCase).toList()).forEach(this::printBookDetails);
    }

    private void searchBookByName(String name) {
        bookService.getBooksByName(name).forEach(this::printBookDetails);
    }

    private void printBookDetails(Book book) {
        console.printf("id: %s, Name: %s, Authors: %s, Price: %s, Release_Date: %s, Tags: %s", book.id(), book.name(), book.authors(), book.price(), InstantFormatter.serialize(book.releaseDate()), book.tags());
        console.printf("\n");
    }

}
