package gr.bookapp.ui;

import gr.bookapp.common.AuditContextImpl;
import gr.bookapp.common.csv.CsvParser;
import gr.bookapp.common.IdGenerator;
import gr.bookapp.common.csv.CsvService;
import gr.bookapp.exceptions.AuthenticationFailedException;
import gr.bookapp.exceptions.CsvFileLoadException;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.Book;
import gr.bookapp.models.Role;
import gr.bookapp.models.User;
import gr.bookapp.repositories.UserRepository;
import gr.bookapp.services.AdminService;
import gr.bookapp.services.AuthenticationService;
import gr.bookapp.services.BookService;
import gr.bookapp.services.UserService;
import gr.bookapp.common.InstantFormatter;

import java.io.Console;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public final class TerminalUI {
    private final Console console ;
    private final IdGenerator idGenerator;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final UserRepository userRepository; //Should this be here ?
    private final AdminService adminService;
    private final BookService bookService;
//    private final CsvParser csvParser;
    private final CsvService csvService;

    public TerminalUI(IdGenerator idGenerator, AuthenticationService authenticationService, UserService userService, UserRepository userRepository, AdminService adminService, BookService bookService, CsvService csvService) {
        this.idGenerator = idGenerator;
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.adminService = adminService;
        this.bookService = bookService;
        this.csvService = csvService;
        this.console = System.console();
    }

    public void start(){
        checkAndCreateAdmin();

        AuditContextImpl.clear();

        loginLoop:
        while (true){
            User user = login();
            if (user == null) continue loginLoop;

            userPanelLoop:
            while (true) {
                if (user.isAdmin()){
                    if (showAdminPanel()) break userPanelLoop;
                }
                else
                    if (showEmployeePanel()) break userPanelLoop;
            }
        }
    }

    private boolean showEmployeePanel() {
        String action = console.readLine("Sell_Book, Search_Book, Add_Book, Delete_Book, Create_Offer, Exit: ");
        switch (action.toLowerCase()) {
            case "sell_book" -> sellBook();
            case "search_book" -> searchBook();
            case "add_book" -> addBook();
            case "delete_book" -> deleteBook();
            case "create_offer" -> createOffer();
            case "exit" -> {return true;}
            default -> System.err.println("Invalid Input !");
        }
        return false;
    }

    private boolean showAdminPanel() {
        String adminAction = console.readLine("Hire_Employee, Fire_Employee, Search_By_Username, Update_Data_From_Csv, Exit :");
        switch (adminAction.toLowerCase()){
            case "hire_employee" -> hireEmployee();
            case "fire_employee" -> fireEmployee();
            case "search_by_username" -> searchEmployeeByUsername();
            case "update_data_from_csv" -> updateDataWithCsv();
            case "exit" -> {return true;}
            default -> System.err.println("Invalid Input !");
        }
        return false;
    }

    private void checkAndCreateAdmin() {
        if (!userService.hasAdminAccount()){
            console.printf("⚠ First-time setup: Create an admin account. \n");

            String username = console.readLine("Enter admin username: ");
            String password = console.readLine("Enter admin password: ");

            User admin = new User(idGenerator.generateID(), username, password, Role.ADMIN);
            try {
                userRepository.add(admin);
            } catch (InvalidInputException e) {
                System.err.println("Username already exist !");
                checkAndCreateAdmin();
            }
            System.out.println("✅ Admin account created successfully.");
        }
    }

    private User login() {
        User user;
        String username = console.readLine("Username: ");
        String password = console.readLine("Password: ");
        try {
            user = authenticationService.authenticate(username, password);
            AuditContextImpl.set(user.id());
        } catch (AuthenticationFailedException e) {
            System.err.println("Authentication failed! Please try again.");
            return null;
        }
        return user;
    }

    private void updateDataWithCsv() {
        String typeOfUpdate = console.readLine("Update Books, BookSales, Users, Offers: ");
        switch (typeOfUpdate.toLowerCase()) {
            case "books" -> {
                try {
                    csvService.updateBooks(console.readLine("Path of Books.csv: "));
                } catch (CsvFileLoadException e) { System.err.println(e.getMessage()); }
            }
            case "booksales" -> {
                try {
                    csvService.updateBookSales(console.readLine("Path of BookSales.csv: "));
                } catch (CsvFileLoadException e) { System.err.println(e.getMessage()); }
            }
            case "users" -> {
                try {
                    csvService.updateUsers(console.readLine("Path of Users.csv: "));
                } catch (InvalidInputException e) { System.err.println("A Username already exist ! (Update failed)");
                } catch (CsvFileLoadException e) { System.err.println(e.getMessage()); }
            }
            case "offers" -> {
                try {
                    csvService.updateOffers(console.readLine("Path of Offers.csv: "));
                } catch (CsvFileLoadException e) { System.err.println(e.getMessage()); }
            }
            default -> System.err.println("Invalid input !");
        }
    }





    private void searchEmployeeByUsername() {
        String username = console.readLine("Username: ");
        User user = adminService.searchEmployeeByUsername(username);
        if (user == null) {
            System.err.println("Employee not found");
            return;
        }
        System.out.printf("Employee_ID: %s, Username: %s, Password: %s%n", user.id(), user.username(), user.password());
    }

    private void fireEmployee() {
        try {
            long employeeID = Long.parseLong(console.readLine("Employee_ID: "));
            adminService.fireEmployee(employeeID);
        }catch (NumberFormatException e) {
            System.err.println("Invalid input !");
        }
    }

    private void hireEmployee() {
        String username = console.readLine("Username: ");
        String password = console.readLine("Password: ");
        try {
            adminService.hireEmployee(username, password);
        } catch (InvalidInputException e) {
            System.err.println(e.getMessage());
        }
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

            long id = idGenerator.generateID();
            book = new Book(id, name, authors, price, releaseDate, tags);

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
