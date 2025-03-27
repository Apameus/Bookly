package gr.bookapp.blackbox;

import gr.bookapp.common.AuditContextImpl;
import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.Book;
import gr.bookapp.models.Offer;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

public final class UserServiceBlackBoxTest extends AbstractBlackBoxTest {

    @Test
    @DisplayName("Sell Book Test")
    void sellBookTest() throws InvalidInputException {
        AuditContextImpl.set(7);
        Book book = Instancio.of(Book.class)
                .set(field(Book::releaseDate), clock.instant())
                .create();
        bookService.addBook(book);

        Book bookToSell = employeeService.sellBook(book.id());
        assertThat(bookToSell).isEqualTo(book);

        assertThat(bookSalesRepository.getBookSalesByBookID(book.id()).sales()).isEqualTo(1);
    }

    @Test
    @DisplayName("Sell Book With Offer")
    void sellBookWithOffer() throws InvalidInputException {
        AuditContextImpl.set(7);

        //Book
        Book book = Instancio.of(Book.class)
                .set(field(Book::releaseDate), clock.instant())
                .set(field(Book::price), 10)
                .create();
        bookService.addBook(book);

        //Offer
        Offer offer = new Offer(333, book.tags(), 40, clock.instant());
        offerRepository.add(offer);

        //Returned value
        Book bookToSell = employeeService.sellBook(book.id());
        double updatedPrice = book.price() - (book.price() * offer.percentage() / 100.0);
        assertThat(bookToSell).isEqualTo(book.withPrice(updatedPrice));
    }

    @Test
    @DisplayName("Sell book with multiple offers")
    void sellBookWithMultipleOffers() throws InvalidInputException {
        AuditContextImpl.set(7);

        //Book
        Book book = Instancio.of(Book.class)
                .set(field(Book::releaseDate), clock.instant())
                .create();
        bookService.addBook(book);

        //Offer
        Offer offer1 = new Offer(333, book.tags(), 15, clock.instant());
        Offer offer2 = new Offer(444, book.tags(), 40, clock.instant());
        Offer bestValidOffer = new Offer(666, book.tags(), 80, clock.instant());
        Offer expiredOffer = new Offer(777, book.tags(), 90, clock.instant().minus(2, ChronoUnit.DAYS));
        Offer offerForOtherTags = new Offer(555, List.of("somethingElse"), 90, clock.instant());
        offerRepository.add(offer1);
        offerRepository.add(offer2);
        offerRepository.add(bestValidOffer);
        offerRepository.add(expiredOffer);
        offerRepository.add(offerForOtherTags);

        //Returned value
        Book bookToSell = employeeService.sellBook(book.id());
        double updatedPrice = book.price() - (book.price() * bestValidOffer.percentage() / 100.0);
        assertThat(bookToSell).isEqualTo(book.withPrice(updatedPrice));
    }
}
