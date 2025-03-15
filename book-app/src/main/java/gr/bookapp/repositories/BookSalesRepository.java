package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.models.BookSales;

public final class BookSalesRepository {
    private final Database<Long, BookSales> bookSalesDatabase;

    public BookSalesRepository(Database<Long, BookSales> bookSalesDatabase) {
        this.bookSalesDatabase = bookSalesDatabase;
    }

    public void add(BookSales bookSales){
        bookSalesDatabase.insert(bookSales.bookID(), bookSales);
    }

    public BookSales getBookSalesByBookID(long bookID){
        return bookSalesDatabase.retrieve(bookID);
    }

}
