package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.database.Index;
import gr.bookapp.database.RangeIndex;
import gr.bookapp.models.Book;

import java.util.*;

public final class BookRepository {

    Database<Long, Book> bookDatabase;
    Index<Book, String> nameIndex = Book::name;
    Index<Book, List<String>> authorIndex = Book::author;
    Index<Book, List<String>> tagIndex = Book::tags;
    RangeIndex<Book, Double> priceRangeIndex = RangeIndex.of(Book::price, Double::compareTo);
    RangeIndex<Book, Long> releaseDateRangeIndex = RangeIndex.of(Book::releaseDate, Long::compareTo);

    public List<Book> findBooksWithName(String name){
        return bookDatabase.findAllBy(nameIndex, name);
    }

    public List<Book> findBooksWithAuthor(List<String> author){
        return bookDatabase.findAllBy(authorIndex, author);
    }

    public List<Book> findBooksWithTag(List<String> tag){
        return bookDatabase.findAllBy(tagIndex, tag);
    }

    public List<Book> findBooksInPriceRange(double min, double max){ return bookDatabase.findAllInRange(priceRangeIndex, min, max); }

    public List<Book> findBooksInDateRange(long min, long max){ return bookDatabase.findAllInRange(releaseDateRangeIndex, min, max); }

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
