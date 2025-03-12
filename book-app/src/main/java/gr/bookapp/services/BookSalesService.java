package gr.bookapp.services;

import gr.bookapp.exceptions.BookNotFoundException;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.BookSales;
import gr.bookapp.repositories.BookSalesRepository;

public final class BookSalesService {
    private final BookSalesRepository bookSalesRepository;

    public BookSalesService(BookSalesRepository bookSalesRepository) {
        this.bookSalesRepository = bookSalesRepository;
    }

    public void increaseSalesOfBook(long bookID, int quantity) throws BookNotFoundException, InvalidInputException {
        BookSales bookSales = bookSalesRepository.getBookSalesByBookID(bookID);
        if (bookSales == null) throw new BookNotFoundException();
        if (quantity <= 0) throw new InvalidInputException("Quantity must be greater than 0");
        bookSalesRepository.add(bookSales.withSales(bookSales.sales() + quantity));
    }
    public void increaseSalesOfBook(long bookID) throws BookNotFoundException {
        try {
            increaseSalesOfBook(bookID, +1);
        } catch (InvalidInputException e) {
            throw new RuntimeException(e);
        }
    }
}
