package gr.bookapp.repositories;

import gr.bookapp.common.IdGenerator;
import gr.bookapp.database.Database;
import gr.bookapp.database.Index;
import gr.bookapp.database.RangeIndex;
import gr.bookapp.models.Book;

import java.time.Instant;
import java.util.*;

public final class BookRepositoryDbImpl implements BookRepository {

    private final Database<Long, Book> bookDatabase;
    private final Index<Book, String> nameIndex = Book::name;
    private final Index<Book, List<String>> authorIndex = Book::authors;
    private final Index<Book, List<String>> tagIndex = Book::tags;
    private final RangeIndex<Book, Double> priceRangeIndex = RangeIndex.of(Book::price, Double::compareTo);
    private final IdGenerator idGenerator;

    public BookRepositoryDbImpl(Database<Long, Book> bookDatabase, IdGenerator idGenerator) {
        this.bookDatabase = bookDatabase;
        this.idGenerator = idGenerator;
    }

    private final RangeIndex<Book, Instant> releaseDateRangeIndex = RangeIndex.of(Book::releaseDate, Instant::compareTo);

    @Override
    public List<Book> findBooksWithName(String name){
        return bookDatabase.findAllByIndex(nameIndex, name);
    }

    @Override
    public List<Book> findBooksWithAuthors(List<String> authors){
        ArrayList<Book> books = new ArrayList<>();
        authors.forEach(author -> books.addAll(bookDatabase.findAllByIndexWithKeys(authorIndex, author)));
        return books;
    }

    @Override
    public List<Book> findBooksWithTags(List<String> tags){
        ArrayList<Book> books = new ArrayList<>();
        tags.forEach(tag -> books.addAll(bookDatabase.findAllByIndexWithKeys(tagIndex, tag)));
        return books;
    }

    @Override
    public List<Book> findBooksInPriceRange(double min, double max){ return bookDatabase.findAllInRange(priceRangeIndex, min, max); }

    @Override
    public List<Book> findBooksInDateRange(Instant from, Instant to){ return bookDatabase.findAllInRange(releaseDateRangeIndex, from, to); }

    @Override
    public List<Book> getAllBooks(){
        return bookDatabase.findAll();
    }

    @Override
    public void add(Book book){
        book = book.withID(idGenerator.generateID(), book);
        bookDatabase.insert(book.id(), book);
    }

    @Override
    public void deleteBookByID(long bookID){
        bookDatabase.delete(bookID);
    }

    @Override
    public Book getBookByID(long bookID){
        return bookDatabase.retrieve(bookID);
    }

}
