package client;

import gr.bookapp.exceptions.AuthenticationFailedException;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.User;
import org.instancio.Instancio;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled
public final class AuthenticationServiceBlackBoxTestClient extends ClientAbstractBlackBoxTest {

    @Test
    @DisplayName("Authentication test")
    void authenticationTest() throws AuthenticationFailedException, InvalidInputException {
        User user = Instancio.create(User.class);
        employeeRepository.add(user);

        assertThat(authenticationService
                .authenticate(user.username(), user.password()))
                .isEqualTo(user);
    }

    @Test
    @DisplayName("Invalid credentials authentication test")
    void invalidCredentialsAuthenticationTest() throws InvalidInputException {
        User user = Instancio.create(User.class);
        employeeRepository.add(user);

        assertThrows(AuthenticationFailedException.class, () -> authenticationService
                .authenticate(user.username(), ""));
    }
}
