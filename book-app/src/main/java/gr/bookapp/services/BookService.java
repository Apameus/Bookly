package gr.bookapp.services;
import gr.bookapp.common.AuditContext;
import gr.bookapp.models.Book;
import gr.bookapp.repositories.AuditRepository;
import gr.bookapp.repositories.BookRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

public final class BookService {

    private final BookRepository bookRepository;
    private final AuditRepository auditRepository;
    private final AuditContext auditContext;
    private final Clock clock;

    public BookService(BookRepository bookRepository, AuditRepository auditRepository, AuditContext auditContext, Clock clock) {
        this.bookRepository = bookRepository;
        this.auditRepository = auditRepository;
        this.auditContext = auditContext;
        this.clock = clock;
    }

    public void addBook(Book book){
        bookRepository.add(book);
        auditRepository.audit(auditContext.getEmployeeID(), "Book added", clock.instant());
    }

    public void deleteBookByID(long bookID){
        bookRepository.deleteBookByID(bookID);
        auditRepository.audit(auditContext.getEmployeeID(), "Book deleted", clock.instant());
    }


    public Book getBookByID(long bookID)   {
        return bookRepository.getBookByID(bookID);
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

    public List<Book> getBooksInPriceRange(double min, double max)   {
        return bookRepository.findBooksInPriceRange(min, max);
    }

    public List<Book> getBooksInDateRange(Instant from, Instant to){
        return bookRepository.findBooksInDateRange(from, to);
    }

}
