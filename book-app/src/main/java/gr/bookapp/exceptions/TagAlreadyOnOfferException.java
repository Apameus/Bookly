package gr.bookapp.exceptions;

import gr.bookapp.models.Offer;

import java.util.List;

public final class TagAlreadyOnOfferException extends Throwable {
    public TagAlreadyOnOfferException(List<Offer> offers) {
        super("The following tags currently have an offer: " + offers); //TODO refactor?
    }
}
