package gr.bookapp.blackbox;

import gr.bookapp.common.AuditContextImpl;
import gr.bookapp.models.Book;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@Disabled
public final class BookSalesServiceBlackBoxTest extends AbstractBlackBoxTest{

    @BeforeEach
    void setup(){
        AuditContextImpl.set(7);
    }

    @Test
    @DisplayName("Increase book sales test")
    void increaseBookSalesTest() {
        Book book = Instancio.create(Book.class);
        bookService.addBook(book);

        bookSalesService.increaseSalesOfBook(book.id());
        assertThat(bookSalesRepository.getBookSalesByBookID(book.id())).isEqualTo(1);
    }

}
