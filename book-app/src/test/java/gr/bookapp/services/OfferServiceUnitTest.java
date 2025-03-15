package gr.bookapp.services;

import gr.bookapp.common.AuditContext;
import gr.bookapp.common.IdGenerator;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.Offer;
import gr.bookapp.repositories.AuditRepository;
import gr.bookapp.repositories.OfferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import static org.mockito.Mockito.*;

class OfferServiceUnitTest {
    OfferRepository offerRepository = Mockito.mock(OfferRepository.class);
    IdGenerator idGenerator = Mockito.mock(IdGenerator.class);
    AuditRepository auditRepository = Mockito.mock(AuditRepository.class);
    AuditContext auditContext = Mockito.mock(AuditContext.class);
    Clock clock = Mockito.mock(Clock.class);
    OfferService offerService;

    @BeforeEach
    void initialize(){
//        clock = Clock.fixed(fixedInstant, zone);
        Instant fixedInstant = Instant.parse("2030-01-01T00:00:00Z");
        ZoneId zone = ZoneId.of("UTC");
        when(clock.instant()).thenReturn(fixedInstant);
        when(clock.getZone()).thenReturn(zone);
        offerService = new OfferService(offerRepository, idGenerator, auditRepository, auditContext, clock);
    }

    @Test
    @DisplayName("Create offer test")
    void createOfferTest() throws InvalidInputException {
        long offerId = 111L;
        int percentage = 15;
        long durationInDays = 30;
        List<String> tags = List.of("Comedy");
        Instant now = clock.instant();
        Instant untilDate = now.plus(durationInDays, ChronoUnit.DAYS);

        Offer offer = new Offer(offerId , tags, percentage, untilDate);

        when(idGenerator.generateID()).thenReturn(offerId);
        when(auditContext.getEmployeeID()).thenReturn(999L);
        offerService.createOffer(tags, percentage, durationInDays);

        verify(offerRepository, times(1)).add(offer);

        String action = "Offer created with ID: %s TAGS: %s PERCENTAGE: %s UNTIL: %s".formatted(offer.offerID(), offer.tags(), offer.percentage(), offer.untilDate());
        verify(auditRepository, times(1)).audit(999, action, now);
    }

    @Test
    @DisplayName("Create offer with invalid inputs test")
    void createOfferWithInvalidInputsTest() {
        long offerId = 111L;
        int percentage = 0;
        long durationInDays = -3;
        List<String> tags = List.of("");

        Instant now = clock.instant();
        Instant untilDate = now.plus(durationInDays, ChronoUnit.DAYS);
        Offer offer = new Offer(offerId , tags, percentage, untilDate);

        when(idGenerator.generateID()).thenReturn(offerId);
        when(auditContext.getEmployeeID()).thenReturn(999L);
        assertThrows(InvalidInputException.class, () -> offerService.createOffer(tags, percentage, durationInDays));
    }
}
