package gr.bookapp.blackbox;

import gr.bookapp.exceptions.AuthenticationFailedException;
import gr.bookapp.models.Employee;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class AuthenticationServiceBlackBoxTest extends AbstractBlackBoxTest{

    @Test
    @DisplayName("Authentication test")
    void authenticationTest() throws AuthenticationFailedException {
        Employee employee = Instancio.create(Employee.class);
        employeeRepository.add(employee);

        assertThat(authenticationService
                .authenticate(employee.username(), employee.password()))
                .isEqualTo(employee);
    }

    @Test
    @DisplayName("Invalid credentials authentication test")
    void invalidCredentialsAuthenticationTest() {
        Employee employee = Instancio.create(Employee.class);
        employeeRepository.add(employee);

        assertThrows(AuthenticationFailedException.class, () -> authenticationService
                .authenticate(employee.username(), ""));
    }
}
