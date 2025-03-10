package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.models.Offer;

public final class OfferRepository {

    Database<Long , Offer> offerDatabase;

    public void add(Offer offer){
        offerDatabase.insert(offer.offerID(), offer);
    }
    
    public void delete(long offerID){
        offerDatabase.delete(offerID);
    }

    public Offer get(long offerID){
        return offerDatabase.retrieve(offerID);
    }
}
