package gr.bookapp.services;

import gr.bookapp.common.AuditContext;
import gr.bookapp.common.IdGenerator;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.log.Logger;
import gr.bookapp.models.Offer;
import gr.bookapp.repositories.AuditRepository;
import gr.bookapp.repositories.OfferRepository;
import gr.bookapp.common.InstantFormatter;

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
    private final Logger logger;

    public OfferService(OfferRepository offerRepository, IdGenerator idGenerator, AuditRepository auditRepository, AuditContext auditContext, Clock clock, Logger.Factory loggerFactory) {
        this.offerRepository = offerRepository;
        this.idGenerator = idGenerator;
        this.auditContext = auditContext;
        this.auditRepository = auditRepository;
        this.clock = clock;
        logger = loggerFactory.create("Offer_Service");
    }

    public void createOffer(List<String> tags, int percentage, Duration duration) throws InvalidInputException {
        if (tags.isEmpty()){
            logger.log("Offer creation failed due to empty tag list");
            throw new InvalidInputException("Tags can't be empty");
        }

        if (percentage <= 0 || percentage >= 100){
            logger.log("Offer creation failed due to invalid percentage");
            throw new InvalidInputException("Percentage must be greater than 0");
        }

        if (duration.isNegative() || duration.isZero()){
            logger.log("Offer creation failed due to invalid duration");
            throw new InvalidInputException("Invalid date");
        }
        Instant now = Instant.now(clock);
        Instant untilDate = now.plus(duration);

        long id = idGenerator.generateID();
        Offer offer = new Offer(id, tags, percentage, untilDate);
        offerRepository.add(offer);

        String action = "Offer created with ID: %s TAGS: %s PERCENTAGE: %s UNTIL: %s"
                .formatted(offer.offerID(), offer.tags(), offer.percentage(), InstantFormatter.serialize(offer.untilDate()));

        auditRepository.audit(auditContext.getEmployeeID(), action, now);

        logger.log("Offer created");
    }


    public List<Offer> getOffers(List<String> tags) {
        return offerRepository.getOffersByTags(tags);
    }
}
