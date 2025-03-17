package gr.bookapp.repositories;

import gr.bookapp.database.Database;
import gr.bookapp.database.Index;
import gr.bookapp.models.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class BookRepositoryTest { //TODO ADD TESTS

    Database<Long, Book> bookDatabase = Mockito.mock(Database.class);
    BookRepository bookRepository = new BookRepository(bookDatabase);
    
    Clock clock = Mockito.mock(Clock.class);
    Book book1 = new Book(1, "Odyssey", List.of("Omiros"), 100, clock.instant(), List.of("Philosophy", "Adventure"));
    Book book2 = new Book(2, "Captain Michalis", List.of("Kazantzakis"), 75, clock.instant(), List.of("Philosophy"));
    Book book3 = new Book(3, "Random", List.of("Manolis", "Manousos"), 32, clock.instant(), List.of("Random", "Adventure"));

    @Test
    @DisplayName("Find all books with specified name")
    void findAllBooksWithSpecifiedName() {
        Index<Book, String> nameIndex = Book::name;
        when(bookDatabase.findAllByIndex(nameIndex, book1.name())).thenReturn(List.of(book1));
        assertThat(bookRepository.findBooksWithName(book1.name())).contains(book1);
    }
}