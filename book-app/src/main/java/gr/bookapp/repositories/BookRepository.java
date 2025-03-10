package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.database.Index;
import gr.bookapp.database.RangeIndex;
import gr.bookapp.models.Book;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class BookRepository {

    Database<Long, Book> bookDatabase;
    Index<Book, String> nameIndex = Book::name;
    Index<Book, List<String>> authorIndex = Book::author;
    Index<Book, List<String>> tagIndex = Book::tags;
    RangeIndex<Book, Double> priceRangeIndex;
    RangeIndex<Book, Long> releaseDateRangeIndex;

    public List<Book> findBooksWithName(String name){
        return bookDatabase.findAllBy(nameIndex, name);
    }

    public List<Book> findBooksWithAuthor(List<String> author){
        return bookDatabase.findAllBy(authorIndex, author);
    }

    public List<Book> findBooksWithTag(List<String> tag){
        return bookDatabase.findAllBy(tagIndex, tag);
    }

    public List<Book> findBooksInPriceRange(double min, double max){
        var books = new ArrayList<Book>();
        var entryIterator = bookDatabase.entryIterator();
        while (entryIterator.hasNext()) {
            Book book = entryIterator.next().getValue();
            if (book.price() >= min && book.price() <= max) books.add(book);
        }
        return books;
    }

    public List<Book> findBooksInDateRange(long min, long max){
        var books = new ArrayList<Book>();
        var entryIterator = bookDatabase.entryIterator();
        while (entryIterator.hasNext()) {
            Book book = entryIterator.next().getValue();
            if (book.releaseDate() >= min && book.releaseDate() <= max) books.add(book);
        }
        return books;
    }

    public void add(Book book){
        bookDatabase.insert(book.id(), book);
    }

    public void delete(long bookID){
        bookDatabase.delete(bookID);
    }

    public Book get(long bookID){
        return bookDatabase.retrieve(bookID);
    }

}
