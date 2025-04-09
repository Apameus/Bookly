package gr.bookapp.services;

import gr.bookapp.exceptions.AuthenticationFailedException;
import gr.bookapp.log.Logger;
import gr.bookapp.models.User;
import gr.bookapp.repositories.UserRepository;

public final class AuthenticationService {
    private final UserRepository employeeRepository;
    private final Logger logger;

    public AuthenticationService(UserRepository userRepository, Logger.Factory loggerFactory) {
        this.employeeRepository = userRepository;
        logger = loggerFactory.create("Authentication_Service");
    }

    public User authenticate(String username, String password) throws AuthenticationFailedException {
        User user = employeeRepository.getUserByUsername(username);
        if (user == null || !user.password().equals(password)) {
            logger.log("Authentication failed !");
            throw new AuthenticationFailedException();
        }
        logger.log("Employee authenticated");
        return user;
    }
}
