package gr.bookapp.services;

import gr.bookapp.AuditContext;
import gr.bookapp.exceptions.BookNotFoundException;
import gr.bookapp.models.Book;
import gr.bookapp.repositories.AuditRepository;
import gr.bookapp.repositories.BookRepository;
import gr.bookapp.repositories.BookSalesRepository;

import java.util.List;

public final class BookService {

    private final BookRepository bookRepository;
    private final AuditRepository auditRepository;

    public BookService(BookRepository bookRepository, AuditRepository auditRepository) {
        this.bookRepository = bookRepository;
        this.auditRepository = auditRepository;
    }

    public void addBook(Book book){
        bookRepository.add(book);
        auditRepository.audit(AuditContext.getEmployeeID(), "Book added", System.currentTimeMillis());
    }

    public void deleteBookByID(long bookID){
        bookRepository.deleteBookByID(bookID);
        auditRepository.audit(AuditContext.getEmployeeID(), "Book deleted", System.currentTimeMillis());
    }

    /**
     *
     * @param bookID
     * @return the Book if exist or BookNotFoundException if not
     * @throws BookNotFoundException
     */
    public Book getBookByID(long bookID) throws BookNotFoundException {
        Book book = bookRepository.getBookByID(bookID);
        if (book == null) throw new BookNotFoundException();
        return book;
    }

    public List<Book> getBooksByName(String name){
        return bookRepository.findBooksWithName(name);
    }

    public List<Book> getBooksByAuthors(List<String> authors){
        return bookRepository.findBooksWithAuthors(authors);
    }

    public List<Book> getBooksByTags(List<String> tags){
        return bookRepository.findBooksWithTags(tags);
    }

    public List<Book> getBooksInPriceRange(double min, double max) throws BookNotFoundException {
        List<Book> books = bookRepository.findBooksInPriceRange(min, max);
        if (books.isEmpty()) throw new BookNotFoundException(); //remove
        return books;
    }

    public List<Book> getBooksInDateRange(long from, long to){
        return bookRepository.findBooksInDateRange(from, to);
    }

}
