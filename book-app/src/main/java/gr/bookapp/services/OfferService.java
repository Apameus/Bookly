package gr.bookapp.services;

import gr.bookapp.AuditContext;
import gr.bookapp.IdGenerator;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.exceptions.TagsAlreadyOnOfferException;
import gr.bookapp.models.Audit;
import gr.bookapp.models.Offer;
import gr.bookapp.repositories.AuditRepository;
import gr.bookapp.repositories.OfferRepository;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

public final class OfferService {
    private final OfferRepository offerRepository;
    private final IdGenerator idGenerator;
    private final AuditRepository auditRepository;

    public OfferService(OfferRepository offerRepository, IdGenerator idGenerator, AuditRepository auditRepository) {
        this.offerRepository = offerRepository;
        this.idGenerator = idGenerator;
        this.auditRepository = auditRepository;
    }

    public void createOffer(Offer offer){
        offerRepository.add(offer);
    }
    public void createOffer(List<String> tags, int percentage, long untilDate) throws InvalidInputException {
        if (percentage <= 0) throw new InvalidInputException("Percentage must be greater than 0");

        long now = System.currentTimeMillis();
        if (untilDate <= now) throw new InvalidInputException("Invalid date");

        long id = idGenerator.generateID();
        Offer offer = new Offer(id, tags, percentage, untilDate);
        offerRepository.add(offer);

        String action = "Offer created with ID: %s TAGS: %s PERCENTAGE: %s UNTIL: %s".formatted(offer.offerID(), offer.tags(), offer.percentage(), offer.untilDate());
        auditRepository.audit(AuditContext.getEmployeeID(), action, System.currentTimeMillis());
    }


    public List<Offer> getOffers(List<String> tags) {
        return offerRepository.getOffersByTags(tags);
    }
}
