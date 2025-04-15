package gr.bookapp.services;

import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.Book;
import gr.bookapp.protocol.packages.Response;
import gr.bookapp.repositories.BookRepository;

import java.time.Instant;
import java.util.List;

public final class BookServiceDbImpl {
    private final BookRepository bookRepository;

    public BookServiceDbImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void addBook(Book book) {
        bookRepository.add(book);
    }

    public void deleteBook(long bookID) throws InvalidInputException {
        if (bookRepository.getBookByID(bookID) == null)
            throw new InvalidInputException("Book with specified id does NOT exist!");
        bookRepository.deleteBookByID(bookID);
    }
    public Book getBookById(long bookID) throws InvalidInputException {
        Book book = bookRepository.getBookByID(bookID);
        if (book == null) throw new InvalidInputException("Book with specified id does NOT exist!");
        return book;
    }
    public List<Book> getAllBooks() throws InvalidInputException {
        List<Book> books = bookRepository.getAllBooks();
        if (books.isEmpty()) throw new InvalidInputException("No books are registered!");
        return books;
    }
    public List<Book> getBooksByName(String name) throws InvalidInputException {
        List<Book> books = bookRepository.findBooksWithName(name);
        if (books.isEmpty()) throw new InvalidInputException("No books with specified name registered!");
        return books;
    }
    public List<Book> getBooksByAuthors(List<String> authors) throws InvalidInputException {
        List<Book> books = bookRepository.findBooksWithAuthors(authors);
        if (books.isEmpty()) throw new InvalidInputException("No books with specified authors registered!");
        return books;
    }
    public List<Book> getBooksByTags(List<String> tags) throws InvalidInputException {
        List<Book> books = bookRepository.findBooksWithTags(tags);
        if (books.isEmpty()) throw new InvalidInputException("No books with specified tags registered!");
        return books;
    }
    public List<Book> getBooksInPriceRange(double min, double max) throws InvalidInputException {
        List<Book> books = bookRepository.findBooksInPriceRange(min, max);
        if (books.isEmpty()) throw new InvalidInputException("No books in specified price-range registered!");
        return books;
    }
    public List<Book> getBooksInDateRange(Instant from, Instant to) throws InvalidInputException {
        List<Book> books = bookRepository.findBooksInDateRange(from, to);
        if (books.isEmpty()) throw new InvalidInputException("No books in specified date-range registered!");
        return books;
    }
}
