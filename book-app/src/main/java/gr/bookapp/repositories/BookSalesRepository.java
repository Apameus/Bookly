package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.models.BookSales;

public final class BookSalesRepository {

    Database<Long, BookSales> bookSalesDatabase;

    public void add(BookSales bookSales){
        bookSalesDatabase.insert(bookSales.bookID(), bookSales);
    }

    public void delete(long bookID){
        bookSalesDatabase.delete(bookID);
    }

    public BookSales get(long bookID){
        return bookSalesDatabase.retrieve(bookID);
    }

}
