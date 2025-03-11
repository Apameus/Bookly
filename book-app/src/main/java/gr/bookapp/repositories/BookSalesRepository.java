package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.models.BookSales;

public final class BookSalesRepository {

    Database<Long, BookSales> bookSalesDatabase;

    public void add(BookSales bookSales){
        bookSalesDatabase.insert(bookSales.bookID(), bookSales);
    }

    public BookSales getSalesByBookID(long bookID){
        return bookSalesDatabase.retrieve(bookID);
    }

}
