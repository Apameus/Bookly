package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.database.Index;
import gr.bookapp.models.Offer;

import java.util.ArrayList;
import java.util.List;

public final class OfferRepository {

    private final Database<Long , Offer> offerDatabase;
    private final Index<Offer, List<String>> tagIndex = Offer::tags;

    public OfferRepository(Database<Long, Offer> offerDatabase) {
        this.offerDatabase = offerDatabase;
    }

    public List<Offer> getOffersByTags(List<String> tags){
        ArrayList<Offer> offers = new ArrayList<>();
        tags.forEach(tag -> offers.addAll(offerDatabase.findAllByIndexWithKeys(tagIndex, tag)));
        return offers;
    }

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
