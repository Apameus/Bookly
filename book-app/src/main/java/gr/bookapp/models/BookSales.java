package gr.bookapp.models;

public record BookSales(long bookID, long sales) {
    public BookSales fromSales(long updatedSales) { return new BookSales(bookID, updatedSales); }
}
