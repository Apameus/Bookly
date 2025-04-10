package gr.bookapp.services;

import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.Offer;
import gr.bookapp.repositories.OfferRepository;

import java.util.List;

public final class OfferServiceDbImpl {
    private final OfferRepository offerRepository;

    public OfferServiceDbImpl(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

      public void createOffer(Offer offer)  {
        offerRepository.add(offer);
    }
    public void deleteOffer(long offerID) throws InvalidInputException {
        if (offerRepository.getOfferById(offerID) == null) throw new InvalidInputException("Offer with specified id does NOT exist!");
        offerRepository.deleteOfferById(offerID);
    }
    public Offer getOfferById(long offerID) throws InvalidInputException {
        Offer offer = offerRepository.getOfferById(offerID);
        if (offer == null) throw new InvalidInputException("Offer with specified id does NOT exist!");
        return offer;
    }
    public List<Offer> getAllOffers() throws InvalidInputException {
        List<Offer> offers = offerRepository.getAllOffers();
        if (offers.isEmpty()) throw new InvalidInputException("No offers are registered!");
        return offers;
    }
    public List<Offer> getOffersByTags(List<String> tags) throws InvalidInputException {
        List<Offer> offers = offerRepository.getOffersByTags(tags);
        if (offers.isEmpty()) throw new InvalidInputException("No offers with specified tags are registered!");
        return offers;
    }
}
