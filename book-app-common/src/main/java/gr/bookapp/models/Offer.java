package gr.bookapp.models;

import java.time.Instant;
import java.util.List;

public record Offer(Long offerID, List<String> tags, int percentage, Instant expirationDate) {

    public Offer(List<String> tags, int percentage, Instant untilDate){ this(null, tags, percentage, untilDate);}

    public Offer setIdAndExpireDate(long setID, Instant setExpireDate) {
        return new Offer(setID, tags, percentage, setExpireDate);
    }

    public Offer withID(long setID) {
        return new Offer(setID, tags, percentage, expirationDate);
    }
    public Offer withUntilDate(Instant setUntilDate){
        return new Offer(offerID, tags, percentage, setUntilDate);
    }

}
