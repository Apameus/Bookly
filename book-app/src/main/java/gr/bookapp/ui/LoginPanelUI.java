package gr.bookapp.ui;

import gr.bookapp.common.AuditContextImpl;
import gr.bookapp.common.IdGenerator;
import gr.bookapp.exceptions.AuthenticationFailedException;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.Role;
import gr.bookapp.models.User;
import gr.bookapp.repositories.UserRepository;
import gr.bookapp.services.AuthenticationService;
import gr.bookapp.services.UserService;

import java.io.Console;

public final class LoginPanelUI {
    private final Console console = System.console();
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final UserRepository userRepository;


    public LoginPanelUI(AuthenticationService authenticationService, UserService userService, UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public User login() {
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

    public void checkAndCreateAdmin() {
        if (!userService.hasAdminAccount()){
            console.printf("⚠ First-time setup: Create an admin account. \n");

            String username = console.readLine("Enter admin username: ");
            String password = console.readLine("Enter admin password: ");

            User admin = new User(username, password, Role.ADMIN);
            try {
                userRepository.add(admin);
            } catch (InvalidInputException e) {
                System.err.println("Username already exist !");
                checkAndCreateAdmin();
            }
            System.out.println("✅ Admin account created successfully.");
        }
    }
}
