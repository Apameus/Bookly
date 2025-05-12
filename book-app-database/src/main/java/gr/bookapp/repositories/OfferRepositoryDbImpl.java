package gr.bookapp.repositories;

import gr.bookapp.common.IdGenerator;
import gr.bookapp.database.Database;
import gr.bookapp.database.Index;
import gr.bookapp.models.Offer;

import java.util.ArrayList;
import java.util.List;

public final class OfferRepositoryDbImpl implements OfferRepository {

    private final Database<Long , Offer> offerDatabase;
    private final Index<Offer, List<String>> tagIndex = Offer::tags;
    private final IdGenerator idGenerator;

    public OfferRepositoryDbImpl(Database<Long, Offer> offerDatabase, IdGenerator idGenerator) {
        this.offerDatabase = offerDatabase;
        this.idGenerator = idGenerator;
    }

    @Override
    public void add(Offer offer){
        //todo: Should we also set the expiration date here ?
        offer = offer.withID(idGenerator.generateID());
        offerDatabase.insert(offer.offerID(), offer);
    }

    @Override
    public void deleteOfferById(long offerID){
        offerDatabase.delete(offerID);
    }

    @Override
    public Offer getOfferById(long offerID){
        return offerDatabase.retrieve(offerID);
    }

    @Override
    public List<Offer> getAllOffers(){ return offerDatabase.findAll(); }

    @Override
    public List<Offer> getOffersByTags(List<String> tags){
        ArrayList<Offer> offers = new ArrayList<>();
        tags.forEach(tag -> offers.addAll(offerDatabase.findAllByIndexWithKeys(tagIndex, tag)));
        return offers;
    }

}
