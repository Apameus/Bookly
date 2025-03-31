package gr.bookapp.repositories;

import gr.bookapp.models.Offer;

import java.util.List;

public interface OfferRepository {
    List<Offer> getOffersByTags(List<String> tags);

    void add(Offer offer);

    void deleteOfferById(long offerID);

    Offer getOfferById(long offerID);

    List<Offer> getAllOffers();
}
