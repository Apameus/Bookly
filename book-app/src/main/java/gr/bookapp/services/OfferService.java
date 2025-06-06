package gr.bookapp.services;

import gr.bookapp.common.IdGenerator;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.log.Logger;
import gr.bookapp.models.Offer;
import gr.bookapp.repositories.OfferRepository;
import gr.bookapp.common.InstantFormatter;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

public final class OfferService {
    private final OfferRepository offerRepository;
    private final Logger logger;
    private final Clock clock;

    public OfferService(OfferRepository offerRepository, Clock clock, Logger.Factory loggerFactory) {
        this.offerRepository = offerRepository;

        logger = loggerFactory.create("Offer_Service");
        this.clock = clock;
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
        Instant now = clock.instant();
        Instant expirationDate = now.plus(duration); //TODO: Should we set the expirationDate here?

        Offer offer = new Offer(tags, percentage, expirationDate);
        offerRepository.add(offer);

        logger.log("Offer created");
    }


    public List<Offer> getOffers(List<String> tags) {
        return offerRepository.getOffersByTags(tags);
    }
}
