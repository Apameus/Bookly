package gr.bookapp.repositories;

import gr.bookapp.client.Client;
import gr.bookapp.models.BookSales;
import gr.bookapp.protocol.packages.Request;
import gr.bookapp.protocol.packages.Response;

import static gr.bookapp.protocol.packages.Request.BookSales.*;

public final class BookSalesRepositoryClientImpl implements BookSalesRepository{
    private final Client client;

    public BookSalesRepositoryClientImpl(Client client) {
        this.client = client;
    }

    @Override
    public void increaseSalesOfBook(long bookID, int quantity) {
        Response response = client.send(new IncreaseSalesOfBookRequest(bookID, quantity));
        switch (response) {
            case Response.GeneralSuccessResponse() -> {}
            case Response.ErrorResponse(String error) -> {}
            default -> throw new IllegalStateException("Unexpected response: " + response);
        }
    }


    /**
     * Overrides the bookSales of the DB with the provided one
     * @param bookSales
     */
    @Override
    public void add(BookSales bookSales) { //TODO Client should NOT have the option to add bookSales but only to increase it. (except CSV)
        Response response = client.send(new OverrideBookSalesRequest(bookSales));
        switch (response) {
            case Response.GeneralSuccessResponse() -> {}
            case Response.ErrorResponse(String error) -> {}
            default -> throw new IllegalStateException("Unexpected response: " + response);
        }
    }

    @Override
    public void delete(long bookID) {} //TODO Client should NOT have the option to delete bookSales.

    @Override
    public BookSales getBookSalesByBookID(long bookID) {
        Response response = client.send(new GetBookSalesRequest(bookID));
        return switch (response) {
            case Response.GetBookSalesResponse(BookSales bookSales) -> bookSales;
            case Response.ErrorResponse(String error) -> null;
            default -> throw new IllegalStateException("Unexpected response: " + response);
        };
    }
}
