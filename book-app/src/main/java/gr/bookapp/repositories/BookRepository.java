package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.database.Index;
import gr.bookapp.database.RangeIndex;
import gr.bookapp.models.Book;

import java.time.Instant;
import java.util.*;

public final class BookRepository {

    private final Database<Long, Book> bookDatabase;
    private final Index<Book, String> nameIndex = Book::name;
    private final Index<Book, List<String>> authorIndex = Book::authors; //todo
    private final Index<Book, List<String>> tagIndex = Book::tags; //todo
    private final RangeIndex<Book, Double> priceRangeIndex = RangeIndex.of(Book::price, Double::compareTo);

    public BookRepository(Database<Long, Book> bookDatabase) {
        this.bookDatabase = bookDatabase;
    }

    private final RangeIndex<Book, Instant> releaseDateRangeIndex = RangeIndex.of(Book::releaseDate, Instant::compareTo);

    public List<Book> findBooksWithName(String name){
        return bookDatabase.findAllByIndex(nameIndex, name);
    }

    public List<Book> findBooksWithAuthors(List<String> authors){ //todo
        ArrayList<Book> books = new ArrayList<>();
        authors.forEach(author -> books.addAll(bookDatabase.findAllByIndexWithKeys(authorIndex, author)));
        return books;
    }

    public List<Book> findBooksWithTags(List<String> tags){
        ArrayList<Book> books = new ArrayList<>();
        tags.forEach(tag -> books.addAll(bookDatabase.findAllByIndexWithKeys(tagIndex, tag)));
        return books;
    }

    public List<Book> findBooksInPriceRange(double min, double max){ return bookDatabase.findAllInRange(priceRangeIndex, min, max); }

    public List<Book> findBooksInDateRange(Instant from, Instant to){ return bookDatabase.findAllInRange(releaseDateRangeIndex, from, to); }

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
