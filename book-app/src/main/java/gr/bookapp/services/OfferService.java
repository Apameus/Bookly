package gr.bookapp.services;

import gr.bookapp.common.AuditContext;
import gr.bookapp.common.IdGenerator;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.Offer;
import gr.bookapp.repositories.AuditRepository;
import gr.bookapp.repositories.OfferRepository;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public final class OfferService {
    private final OfferRepository offerRepository;
    private final IdGenerator idGenerator;
    private final AuditRepository auditRepository;
    private final AuditContext auditContext;
    private final Clock clock;

    public OfferService(OfferRepository offerRepository, IdGenerator idGenerator, AuditRepository auditRepository, AuditContext auditContext, Clock clock) {
        this.offerRepository = offerRepository;
        this.idGenerator = idGenerator;
        this.auditContext = auditContext;
        this.auditRepository = auditRepository;
        this.clock = clock;
    }

    public void createOffer(List<String> tags, int percentage, Duration duration) throws InvalidInputException {
        if (percentage <= 0) throw new InvalidInputException("Percentage must be greater than 0");

        if (duration.isNegative()) throw new InvalidInputException("Invalid date");
        Instant now = Instant.now(clock);
        Instant untilDate = now.plus(duration);

        long id = idGenerator.generateID();
        Offer offer = new Offer(id, tags, percentage, untilDate);
        offerRepository.add(offer);

        String action = "Offer created with ID: %s TAGS: %s PERCENTAGE: %s UNTIL: %s"
                .formatted(offer.offerID(), offer.tags(), offer.percentage(), offer.untilDate());

        auditRepository.audit(auditContext.getEmployeeID(), action, now);
    }


    public List<Offer> getOffers(List<String> tags) {
        return offerRepository.getOffersByTags(tags);
    }
}
