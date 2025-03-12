package gr.bookapp.services;

import gr.bookapp.exceptions.BookNotFoundException;
import gr.bookapp.exceptions.InvalidQuantityException;
import gr.bookapp.models.BookSales;
import gr.bookapp.repositories.BookSalesRepository;

public final class BookSalesService {
    private final BookSalesRepository bookSalesRepository;

    public BookSalesService(BookSalesRepository bookSalesRepository) {
        this.bookSalesRepository = bookSalesRepository;
    }

    public void increaseSalesOfBook(long bookID, int quantity) throws BookNotFoundException, InvalidQuantityException {
        BookSales bookSales = bookSalesRepository.getBookSalesByBookID(bookID);
        if (bookSales == null) throw new BookNotFoundException();
        if (quantity <= 0) throw new InvalidQuantityException();
        bookSales.fromSales(bookSales.sales() + quantity);
        bookSalesRepository.add(bookSales);
    }
    public void increaseSalesOfBook(long bookID) throws BookNotFoundException, InvalidQuantityException {
        increaseSalesOfBook(bookID, +1);
    }
}
