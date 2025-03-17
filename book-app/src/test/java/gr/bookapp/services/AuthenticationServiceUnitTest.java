package gr.bookapp.services;

import gr.bookapp.exceptions.AuthenticationFailedException;
import gr.bookapp.models.Employee;
import gr.bookapp.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class AuthenticationServiceUnitTest {

    EmployeeRepository employeeRepository = Mockito.mock(EmployeeRepository.class);
    AuthenticationService authenticationService;

    @BeforeEach
    void initialise(){
        authenticationService = new AuthenticationService(employeeRepository);
    }

    @Test
    @DisplayName("Authentication test")
    void authenticationTest() throws AuthenticationFailedException {
        Employee employee = new Employee(007, "Manolis", "123");
        when(employeeRepository.getEmployeeByUsername("Manolis")).thenReturn(employee);
        assertThat(authenticationService.authenticate(employee.username(), employee.password())).isEqualTo(employee);
    }

    @Test
    @DisplayName("Failed authentication test")
    void failedAuthenticationTest() {
        Employee employee = new Employee(007, "Manolis", "123");
        when(employeeRepository.getEmployeeByUsername("Manolis")).thenReturn(employee);
        assertThrows(AuthenticationFailedException.class, () -> authenticationService.authenticate(employee.username(), ""));
    }
}