package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.models.BookSales;

public final class BookSalesRepositoryDbImpl implements BookSalesRepository {
    private final Database<Long, BookSales> bookSalesDatabase;

    public BookSalesRepositoryDbImpl(Database<Long, BookSales> bookSalesDatabase) {
        this.bookSalesDatabase = bookSalesDatabase;
    }

    @Override
    public void add(BookSales bookSales){
        bookSalesDatabase.insert(bookSales.bookID(), bookSales);
    }

    @Override
    public void delete(long bookID){
        bookSalesDatabase.delete(bookID);
    }

    @Override
    public BookSales getBookSalesByBookID(long bookID){
        return bookSalesDatabase.retrieve(bookID);
    }

}
