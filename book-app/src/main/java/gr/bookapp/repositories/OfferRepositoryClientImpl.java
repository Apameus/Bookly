package gr.bookapp.repositories;

import gr.bookapp.client.Client;
import gr.bookapp.models.Offer;
import gr.bookapp.protocol.packages.Response;
import java.util.ArrayList;
import java.util.List;
import static gr.bookapp.protocol.packages.Request.Offer.*;
import static gr.bookapp.protocol.packages.Response.*;

public final class OfferRepositoryClientImpl implements OfferRepository {
    private final Client client;

    public OfferRepositoryClientImpl(Client client) {
        this.client = client;
    }

    @Override
    public List<Offer> getOffersByTags(List<String> tags) {
        Response response = client.send(new GetOffersByTagsRequest(tags));
        switch (response) {
            case GetOffersResponse(List<Offer> offers) -> { return offers; }
            case ErrorResponse(String error) -> { return new ArrayList<>();}
            default -> throw new IllegalStateException("Unexpected response: " + response);
        }
    }

    @Override
    public void add(Offer offer) {
        Response response = client.send(new CreateOfferRequest(offer));
        switch (response) {
            case GeneralSuccessResponse() -> {}
            case ErrorResponse(String error) -> {}
            default -> throw new IllegalStateException("Unexpected response: " + response);
        }
    }

    @Override
    public void deleteOfferById(long offerID) {
        Response response = client.send(new DeleteOfferRequest(offerID));
        switch (response) {
            case GeneralSuccessResponse() -> {}
            case ErrorResponse(String error) -> {}
            default -> throw new IllegalStateException("Unexpected response: " + response);
        }
    }

    @Override
    public Offer getOfferById(long offerID) {
        Response response = client.send(new GetOfferByIdRequest(offerID));
        return switch (response) {
            case GetOfferResponse(Offer offer) -> offer;
            case ErrorResponse(String error) -> null;
            default -> throw new IllegalStateException("Unexpected response: " + response);
        };
    }

    @Override
    public List<Offer> getAllOffers() {
        Response response = client.send(new GetAllOffersRequest());
        return switch (response) {
            case GetOffersResponse(List<Offer> offers) -> offers;
            case ErrorResponse(String error) -> null;
            default -> throw new IllegalStateException("Unexpected response: " + response);
        };
    }
}
