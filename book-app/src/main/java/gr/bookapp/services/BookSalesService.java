package gr.bookapp.services;

import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.BookSales;
import gr.bookapp.repositories.BookSalesRepository;

public final class BookSalesService {
    private final BookSalesRepository bookSalesRepository;

    public BookSalesService(BookSalesRepository bookSalesRepository) {
        this.bookSalesRepository = bookSalesRepository;
    }

    public void increaseSalesOfBook(long bookID, int quantity) throws InvalidInputException {
        BookSales bookSales = bookSalesRepository.getBookSalesByBookID(bookID);
        if (quantity <= 0) throw new InvalidInputException("Quantity must be greater than 0");
        bookSalesRepository.add(bookSales.withSales(bookSales.sales() + quantity));
    }
    public void increaseSalesOfBook(long bookID) {
        try {
            increaseSalesOfBook(bookID, +1);
        } catch (InvalidInputException e) {
            throw new RuntimeException(e);
        }
    }
}
