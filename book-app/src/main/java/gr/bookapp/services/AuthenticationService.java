package gr.bookapp.services;

import gr.bookapp.exceptions.AuthenticationFailedException;
import gr.bookapp.log.Logger;
import gr.bookapp.models.Employee;
import gr.bookapp.repositories.EmployeeRepository;

public final class AuthenticationService {
    private final EmployeeRepository employeeRepository;
    private final Logger logger;

    public AuthenticationService(EmployeeRepository employeeRepository, Logger.Factory loggerFactory) {
        this.employeeRepository = employeeRepository;
        logger = loggerFactory.create("Authentication_Service");
    }

    public Employee authenticate(String username, String password) throws AuthenticationFailedException {
        Employee employee = employeeRepository.getEmployeeByUsername(username);
        if (employee == null || !employee.password().equals(password)) {
            logger.log("Authentication failed !");
            throw new AuthenticationFailedException();
        }
        logger.log("Employee authenticated");
        return employee;
    }
}
