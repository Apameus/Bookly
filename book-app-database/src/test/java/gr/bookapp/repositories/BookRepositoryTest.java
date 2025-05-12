package gr.bookapp.repositories;

import gr.bookapp.common.IdGenerator;
import gr.bookapp.database.Database;
import gr.bookapp.database.Index;
import gr.bookapp.models.Book;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Disabled
class BookRepositoryTest { //TODO: TESTS MISSING

    Database<Long, Book> bookDatabase = Mockito.mock(Database.class);
    IdGenerator idGenerator = Mockito.mock(IdGenerator.class);
    BookRepositoryDbImpl bookRepositoryDbImpl = new BookRepositoryDbImpl(bookDatabase, idGenerator);
    
    Clock clock = Clock.fixed(Instant.parse("2018-04-29T10:15:30.00Z"), ZoneOffset.UTC);
    Book book1 = new Book(1, "Odyssey", List.of("Omiros"), 100, clock.instant(), List.of("Philosophy", "Adventure"));
    Book book2 = new Book(2, "Captain Michalis", List.of("Kazantzakis"), 75, clock.instant(), List.of("Philosophy"));
    Book book3 = new Book(3, "Random", List.of("Manolis", "Manousos"), 32, clock.instant(), List.of("Random", "Adventure"));

    @Test
    @DisplayName("Find all books with specified name")
    void findAllBooksWithSpecifiedName() {
        Index<Book, String> nameIndex = Book::name; //todo: this nameIndex is not the same instance with the nameIndex of the impl
        when(bookDatabase.findAllByIndex(nameIndex, book1.name())).thenReturn(List.of(book1));
        List<Book> books = bookRepositoryDbImpl.findBooksWithName(book1.name());
        assertThat(books).contains(book1);
    }
}