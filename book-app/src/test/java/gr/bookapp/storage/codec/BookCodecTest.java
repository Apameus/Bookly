package gr.bookapp.storage.codec;

import gr.bookapp.models.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BookCodecTest {
    StringCodec stringCodec = new StringCodec();
    ListCodec<String> listCodec = new ListCodec<>(stringCodec);
    InstantCodec instantCodec = new InstantCodec(stringCodec);
    BookCodec bookCodec = new BookCodec(stringCodec, listCodec, instantCodec);
    RandomAccessFile accessFile;
    @TempDir Path dir;

    @BeforeEach
    void setup() throws IOException {
        accessFile = new RandomAccessFile(dir.resolve("bookCodec.test").toFile(), "rw");
        accessFile.setLength(1000);
    }

    @Test
    @DisplayName("Write-Read Book")
    void writeReadBook() throws IOException {
        Book book = new Book(111,
                "Odyssey",
                List.of("Omiros"),
                100,
                LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC),
                List.of("Philosophy", "Adventure"));
        bookCodec.write(accessFile, book);
        accessFile.seek(0);
        assertThat(bookCodec.read(accessFile)).isEqualTo(book);
    }
}