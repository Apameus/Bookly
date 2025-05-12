package gr.bookapp.services;

import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.BookSales;
import gr.bookapp.repositories.BookSalesRepository;

public final class BookSalesServiceDbImpl {
    private final BookSalesRepository bookSalesRepository;

    public BookSalesServiceDbImpl(BookSalesRepository bookSalesRepository) {
        this.bookSalesRepository = bookSalesRepository;
    }

    public void overrideBookSales(BookSales bookSales) {
        bookSalesRepository.add(bookSales);
    }

    public void increaseSalesOfBook(long bookID, int quantity) throws InvalidInputException {
        if (bookSalesRepository.getBookSalesByBookID(bookID) == null)
            throw new InvalidInputException("BookSales not found!");
        if (quantity <= 0) throw new InvalidInputException("Quantity must be greater than 0!");
        bookSalesRepository.increaseSalesOfBook(bookID, quantity);
    }

    public BookSales getBookSales(long bookID) throws InvalidInputException {
        BookSales bookSales = bookSalesRepository.getBookSalesByBookID(bookID);
        if (bookSales == null) throw new InvalidInputException("BookSales not found!");
        return bookSales;
    }
}
