package gr.bookapp.protocol.codec;

import gr.bookapp.models.BookSales;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class BookSalesCodecTest {
    @TempDir
    Path dir;
    RandomAccessFile accessFile;
    BookSalesCodec bookSalesCodec = new BookSalesCodec();

    @BeforeEach
    void initialize() throws IOException {
        bookSalesCodec = new BookSalesCodec();
        accessFile = new RandomAccessFile(dir.resolve("BookSales.test").toFile(), "rw");
        accessFile.setLength(1000);
    }

    @Test
    @DisplayName("Write-Read BookSales test")
    void writeReadBookSalesTest() throws IOException {
        BookSales bookSales = new BookSales(555, 13);
        bookSalesCodec.write(accessFile, bookSales);
        accessFile.seek(0);
        assertThat(bookSalesCodec.read(accessFile)).isEqualTo(bookSales);
    }

}