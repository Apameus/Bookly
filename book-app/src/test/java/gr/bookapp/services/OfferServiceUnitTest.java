package gr.bookapp.services;

import gr.bookapp.common.AuditContext;
import gr.bookapp.common.IdGenerator;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.log.Logger;
import gr.bookapp.models.Offer;
import gr.bookapp.repositories.AuditRepository;
import gr.bookapp.repositories.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.*;
import java.util.List;
import static org.mockito.Mockito.*;

class OfferServiceUnitTest {
    OfferRepository offerRepository = Mockito.mock(OfferRepository.class);
    IdGenerator idGenerator = Mockito.mock(IdGenerator.class);
    AuditService auditService = Mockito.mock(AuditService.class);
    AuditContext auditContext = Mockito.mock(AuditContext.class);
    Clock clock = Mockito.mock(Clock.class);
    Logger.Factory logger = Mockito.mock(Logger.Factory.class);
    OfferService offerService;

    @BeforeEach
    void initialize(){
        Instant fixedInstant = Instant.parse("2030-01-01T00:00:00Z");
        when(clock.instant()).thenReturn(fixedInstant);
//        when(Clock.systemUTC()).thenReturn(fixedInstant);
        when(logger.create("Offer_Service")).thenReturn(Mockito.mock(Logger.class));
        offerService = new OfferService(offerRepository, idGenerator, auditService, logger);
    }

    @Test
    @DisplayName("Create offer test")
    void createOfferTest() throws InvalidInputException {
        long offerId = 111L;
        int percentage = 15;
        Duration duration = Duration.ofDays(30);
        List<String> tags = List.of("Comedy");
        Instant now = clock.instant(); //todo fixed instant here but not in the impl
        Instant untilDate = now.plus(duration);

        Offer offer = new Offer(offerId , tags, percentage, untilDate);

        when(idGenerator.generateID()).thenReturn(offerId);
        when(auditContext.getUserID()).thenReturn(999L);
        offerService.createOffer(tags, percentage, duration);

        verify(offerRepository, times(1)).add(offer);

        String action = "Offer created with ID: %s TAGS: %s PERCENTAGE: %s UNTIL: %s".formatted(offer.offerID(), offer.tags(), offer.percentage(), offer.untilDate());
        verify(auditService, times(1)).audit(action);
    }

    @Test
    @DisplayName("Create offer with invalid percentage test")
    void createOfferWithInvalidInputsTest() {
        long offerId = 111L;
        int percentage = 0;
        Duration duration = Duration.ofDays(3);
        List<String> tags = List.of("");

        Instant now = clock.instant();
        Instant untilDate = now.plus(duration);
        Offer offer = new Offer(offerId , tags, percentage, untilDate);

        when(idGenerator.generateID()).thenReturn(offerId);
        when(auditContext.getUserID()).thenReturn(999L);
        assertThrows(InvalidInputException.class, () -> offerService.createOffer(tags, percentage, duration));
    }

    @Test
    @DisplayName("Create offer with invalid duration test")
    void createOfferWithInvalidDurationTest() {
        long offerId = 111L;
        int percentage = 10;
        Duration duration = Duration.ofDays(-3);
        List<String> tags = List.of("");

        Instant now = clock.instant();
        Instant untilDate = now.plus(duration);
        Offer offer = new Offer(offerId , tags, percentage, untilDate);

        when(idGenerator.generateID()).thenReturn(offerId);
        when(auditContext.getUserID()).thenReturn(999L);
        assertThrows(InvalidInputException.class, () -> offerService.createOffer(tags, percentage, duration));
    }
}
