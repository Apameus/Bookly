package gr.bookapp.services;
import gr.bookapp.log.Logger;
import gr.bookapp.models.Book;
import gr.bookapp.models.BookSales;
import gr.bookapp.repositories.BookRepository;
import gr.bookapp.repositories.BookSalesRepository;
import java.time.Instant;
import java.util.List;

public final class BookService {

    private final BookRepository bookRepository;
    private final BookSalesRepository bookSalesRepository;
    private final Logger logger;

    public BookService(BookRepository bookRepository, BookSalesRepository bookSalesRepository, Logger.Factory loggerFactory) {
        this.bookRepository = bookRepository;
        this.bookSalesRepository = bookSalesRepository;
        logger = loggerFactory.create("Book_Service");
    }

    public void addBook(Book book){
        bookRepository.add(book);
        bookSalesRepository.add(new BookSales(book.id(), 0)); //TODO: This is work of the DB
        logger.log("Book added");
    }

    public void deleteBookByID(long bookID){
//        if (bookRepository.getBookByID(bookID) == null) logger.log("Book not found");
        bookRepository.deleteBookByID(bookID);
        bookSalesRepository.delete(bookID);//TODO: This is work of the DB
        logger.log("Book deleted");
    }

    // TODO: Add logs
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

    public List<Book> getAllBooks(){
        return bookRepository.getAllBooks();
    }
}
