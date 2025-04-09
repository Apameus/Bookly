package gr.bookapp.services;

import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.log.Logger;
import gr.bookapp.models.BookSales;
import gr.bookapp.repositories.BookSalesRepository;

public final class BookSalesService {
    private final BookSalesRepository bookSalesRepository;
    private final Logger logger;

    public BookSalesService(BookSalesRepository bookSalesRepository, Logger.Factory loggerFactory) {
        this.bookSalesRepository = bookSalesRepository;
        logger = loggerFactory.create("BookSales_Service");
    }

    public void increaseSalesOfBook(long bookID, int quantity) throws InvalidInputException {
        if (quantity <= 0) throw new InvalidInputException("Quantity must be greater than 0");
        bookSalesRepository.increaseSalesOfBook(bookID, quantity);
        logger.log("BookSales updated: BookID: %s, Extra sales: %s", bookID, quantity);
    }

    public void increaseSalesOfBook(long bookID) {
        try {
            increaseSalesOfBook(bookID, +1);
        } catch (InvalidInputException e) {
            throw new RuntimeException(e);
        }
    }
}
