package gr.bookapp.services;

import gr.bookapp.exceptions.AuthenticationFailedException;
import gr.bookapp.log.Logger;
import gr.bookapp.models.User;
import gr.bookapp.models.Role;
import gr.bookapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class AuthenticationServiceUnitTest {

    UserRepository userRepository = Mockito.mock(UserRepository.class);
    AuthenticationService authenticationService;
    Logger.Factory logger = Mockito.mock(Logger.Factory.class);

    @BeforeEach
    void initialise(){
        authenticationService = new AuthenticationService(userRepository, logger);
    }

    @Test
    @DisplayName("Authentication test")
    void authenticationTest() throws AuthenticationFailedException {
        User user = new User(007, "Manolis", "123", Role.EMPLOYEE);
        when(userRepository.getUserByUsername("Manolis")).thenReturn(user);
        assertThat(authenticationService.authenticate(user.username(), user.password())).isEqualTo(user);
    }

    @Test
    @DisplayName("Failed authentication test")
    void failedAuthenticationTest() throws AuthenticationFailedException {
        User user = new User(007, "Manolis", "123", Role.EMPLOYEE);
        when(userRepository.getUserByUsername("Manolis")).thenReturn(user);
        assertThrows(AuthenticationFailedException.class, () -> authenticationService.authenticate(user.username(), ""));
    }
}