package client;

import gr.bookapp.common.AuditContextImpl;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.Book;
import org.instancio.Instancio;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@Disabled
public final class BookServiceBlackBoxTests extends ClientAbstractBlackBoxTest {

    @Test
    @DisplayName("Add book")
    void addBook() throws InvalidInputException {
        AuditContextImpl.set(7);
        Book book = Instancio.of(Book.class)
                .set(field(Book::releaseDate), clock.instant())
                .create();
        bookService.addBook(book);
        assertThat(bookService.getAllBooks()).containsExactly(book);
    }

    @Test
    @DisplayName("DeleteBook")
    void deleteBook() {
        AuditContextImpl.set(7);
        Book book = Instancio.create(Book.class);
        bookService.addBook(book);
        bookService.deleteBookByID(book.id());
        assertThat(bookService.getAllBooks()).isEmpty();
    }

}
