package gr.bookapp.services;

import gr.bookapp.exceptions.OfferDurationException;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.exceptions.TagAlreadyOnOfferException;
import gr.bookapp.models.Offer;
import gr.bookapp.repositories.OfferRepository;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class OfferService {
    private final OfferRepository offerRepository;

    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public void createOffer(Offer offer){
        offerRepository.add(offer);
    }
    public void createOffer(List<String> tags, int percentage, long untilDate) throws OfferDurationException, InvalidInputException, TagAlreadyOnOfferException {
        List<Offer> offers = offerRepository.getOffersByTags(tags);
        if (!offers.isEmpty()) throw new TagAlreadyOnOfferException(offers); //TODO refactor logic

        if (percentage <= 0) throw new InvalidInputException("Percentage must be greater than 0");

        long now = LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        if (untilDate > 30 || untilDate <= now) throw new OfferDurationException();

        long id = offerRepository.getOfferCount() + 1;
        Offer offer = new Offer(id, tags, percentage, untilDate);
        offerRepository.add(offer);
    }


    public List<Offer> getOffers(List<String> tags) {
        return offerRepository.getOffersByTags(tags);
    }
}
