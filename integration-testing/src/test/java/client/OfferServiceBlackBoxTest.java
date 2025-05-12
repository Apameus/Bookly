package client;

import gr.bookapp.common.AuditContextImpl;
import gr.bookapp.exceptions.InvalidInputException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.time.Duration.ofDays;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@Disabled
public final class OfferServiceBlackBoxTest extends ClientAbstractBlackBoxTest {

    @Test
    @DisplayName("Create an offer test")
    void createAnOfferTest() {
        AuditContextImpl.set(7);
        assertDoesNotThrow(() -> offerService.createOffer(of("A", "B"), 20, ofDays(2)));
    }

    @Test
    @DisplayName("Create an offer with invalid input")
    void createAnOfferWithInvalidInput() {
        AuditContextImpl.set(7);
        assertThrows(InvalidInputException.class, () -> offerService.createOffer(of(), 20, ofDays(1)));
    }
}
