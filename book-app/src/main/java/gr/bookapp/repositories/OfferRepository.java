package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.models.Offer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class OfferRepository {

    Database<Long , Offer> offerDatabase;

    public void add(Offer offer){
        offerDatabase.insert(offer.offerID(), offer);
    }
    
    public void deleteOfferById(long offerID){
        offerDatabase.delete(offerID);
    }

    public Offer getOfferById(long offerID){
        return offerDatabase.retrieve(offerID);
    }

    public List<Offer> getAllOffers(){ return offerDatabase.findAll(); }
}
