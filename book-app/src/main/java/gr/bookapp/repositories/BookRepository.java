package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.database.Index;
import gr.bookapp.database.RangeIndex;
import gr.bookapp.models.Book;

import java.util.*;

public final class BookRepository {

    private final Database<Long, Book> bookDatabase;
    private final Index<Book, String> nameIndex = Book::name;
    private final Index<Book, List<String>> authorIndex = Book::authors;
    private final Index<Book, List<String>> tagIndex = Book::tags;
    private final RangeIndex<Book, Double> priceRangeIndex = RangeIndex.of(Book::price, Double::compareTo);

    public BookRepository(Database<Long, Book> bookDatabase) {
        this.bookDatabase = bookDatabase;
    }

    private final RangeIndex<Book, Long> releaseDateRangeIndex = RangeIndex.of(Book::releaseDate, Long::compareTo);

    public List<Book> findBooksWithName(String name){
        return bookDatabase.findAllByIndex(nameIndex, name);
    }

    public List<Book> findBooksWithAuthors(List<String> author){
        return bookDatabase.findAllByIndex(authorIndex, author);
    }

    public List<Book> findBooksWithTags(List<String> tags){
        return bookDatabase.findAllByIndex(tagIndex, tags);
    }

    public List<Book> findBooksInPriceRange(double min, double max){ return bookDatabase.findAllInRange(priceRangeIndex, min, max); }

    public List<Book> findBooksInDateRange(long min, long max){ return bookDatabase.findAllInRange(releaseDateRangeIndex, min, max); }

    public void add(Book book){
        bookDatabase.insert(book.id(), book);
    }

    public void deleteBookByID(long bookID){
        bookDatabase.delete(bookID);
    }

    public Book getBookByID(long bookID){
        return bookDatabase.retrieve(bookID);
    }

}
