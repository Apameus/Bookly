package gr.bookapp.repositories;

import gr.bookapp.models.BookSales;

public interface BookSalesRepository {
    void add(BookSales bookSales);

    void increaseSalesOfBook(long bookID, int quantity);

    void delete(long bookID);

    BookSales getBookSalesByBookID(long bookID);
}
