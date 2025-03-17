package gr.bookapp.services;

import gr.bookapp.exceptions.InvalidInputException;
import gr.bookapp.models.BookSales;
import gr.bookapp.repositories.BookSalesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookSalesServiceTest {
    BookSalesRepository bookSalesRepository = Mockito.mock(BookSalesRepository.class);
    BookSalesService bookSalesService = new BookSalesService(bookSalesRepository);

    @Test
    @DisplayName("Increase sales of book test")
    void increaseSalesOfBookTest() throws InvalidInputException {
        long bookID = 100L;
        BookSales bookSales = new BookSales(bookID, 20);
        when(bookSalesRepository.getBookSalesByBookID(bookID)).thenReturn(bookSales);

        int quantity = 10;
        bookSalesService.increaseSalesOfBook(bookID, quantity);
        verify(bookSalesRepository, times(1)).add(bookSales.withSales(bookSales.sales() + quantity));
    }

    @Test
    @DisplayName("Increase sales of book by 1")
    void increaseSalesOfBookBy1() {
        long bookID = 100L;
        BookSales bookSales = new BookSales(bookID, 20);
        when(bookSalesRepository.getBookSalesByBookID(bookID)).thenReturn(bookSales);

        bookSalesService.increaseSalesOfBook(bookID);
        verify(bookSalesRepository, times(1)).add(bookSales.withSales(bookSales.sales() + 1));
    }

    @Test
    @DisplayName("Increase sales of book with invalid quantity")
    void increaseSalesOfBookWithInvalidQuantity() {
        long bookID = 100L;
        assertThrows( InvalidInputException.class, () -> bookSalesService.increaseSalesOfBook(bookID, -10));
    }

}