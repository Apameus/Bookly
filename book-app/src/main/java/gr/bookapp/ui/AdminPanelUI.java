package gr.bookapp.ui;

import gr.bookapp.common.csv.CsvService;
import gr.bookapp.exceptions.CsvFileLoadException;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.User;
import gr.bookapp.services.AdminService;

import java.io.Console;

public final class AdminPanelUI {
    private final Console console = System.console();
    private final CsvService csvService;
    private final AdminService adminService;
    private final boolean EXIT_COMMAND = false;

    public AdminPanelUI(CsvService csvService, AdminService adminService) {
        this.csvService = csvService;
        this.adminService = adminService;
    }

    public boolean handleAdminActions() {
        String adminAction = console.readLine("Hire_Employee, Fire_Employee, Search_By_Username, Update_Data_From_Csv, Exit :");
        switch (adminAction.toLowerCase()){
            case "hire_employee" -> hireEmployee();
            case "fire_employee" -> fireEmployee();
            case "search_by_username" -> searchEmployeeByUsername();
            case "update_data_from_csv" -> updateDataWithCsv();
            case "exit" -> {return EXIT_COMMAND;}
            default -> System.err.println("Invalid Input !");
        }
        return true;
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

}
