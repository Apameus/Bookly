package gr.bookapp.services;

import gr.bookapp.exceptions.AuthenticationFailedException;
import gr.bookapp.models.Employee;
import gr.bookapp.repositories.EmployeeRepository;

public final class AuthenticationService {
    private final EmployeeRepository employeeRepository;

    public AuthenticationService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee authenticate(String username, String password) throws AuthenticationFailedException {
        Employee employee = employeeRepository.getEmployeeByUsername(username);
        if (employee == null || !employee.password().equals(password)) throw new AuthenticationFailedException();
        return employee;
    }
}
