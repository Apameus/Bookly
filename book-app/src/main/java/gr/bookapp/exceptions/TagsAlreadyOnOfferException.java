package gr.bookapp.exceptions;

import gr.bookapp.models.Offer;

import java.util.List;

public final class TagsAlreadyOnOfferException extends Exception {
    public TagsAlreadyOnOfferException(List<Offer> offers) {
        super("The following tags currently have an offer: " + offers); //TODO refactor?
    }
}
