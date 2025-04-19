package server;

import gr.bookapp.models.Role;
import gr.bookapp.models.User;
import gr.bookapp.protocol.packages.Request.User.AuthenticateRequest;
import gr.bookapp.protocol.packages.Response;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class TCPServerIntegrationTest extends ServerAbstractIntegrationTest {

    @BeforeEach
    void setRepos(){
        when(userRepository.getUserByUsername("makis")).thenReturn(new User("makis", "makis123", Role.EMPLOYEE));
        when(userRepository.getUserByID(1)).thenReturn(new User("admin", "admin123", Role.ADMIN));
    }

    @Test
    @DisplayName("If AuthorizeRequest with valid credentials return AuthenticateResponse")
    void ifAuthorizeRequestWithValidCredentialsReturnAuthenticateResponse() {
        assertThat(client.send(new AuthenticateRequest("makis", "makis123"))).isEqualTo(new Response.AuthenticateResponse());
    }

    @Test
    @DisplayName("If AuthenticateRequest with invalid credentials return ErrorResponse")
    void ifAuthenticateRequestWithInvalidCredentialsReturnErrorResponse() {
        assertThat(client.send(new AuthenticateRequest("fail", "fail"))).isEqualTo(new Response.ErrorResponse("Authentication failed!"));
    }



}