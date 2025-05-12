package gr.bookapp.protocol.codec;

import gr.bookapp.models.Book;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

class BookCodecTest {
    StringCodec stringCodec = new StringCodec();
    ListCodec<String> listCodec = new ListCodec<>(stringCodec);
    InstantCodec instantCodec = new InstantCodec();
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

    @Test
    @DisplayName("Write-Read multiple books")
    void writeReadMultipleBooks() throws IOException {
        Book bookA = Instancio.of(Book.class)
                .set(field(Book::releaseDate), Clock.systemUTC())
                .create();
        Book bookB = Instancio.create(Book.class);
        Book bookC = Instancio.create(Book.class);
        Book bookD = Instancio.create(Book.class);

        bookCodec.write(accessFile, bookA);
        bookCodec.write(accessFile, bookB);
        bookCodec.write(accessFile, bookC);
        bookCodec.write(accessFile, bookD);

        accessFile.seek(0);

        assertThat(bookCodec.read(accessFile)).isEqualTo(bookA);
        assertThat(bookCodec.read(accessFile)).isEqualTo(bookB);
        assertThat(bookCodec.read(accessFile)).isEqualTo(bookC);
        assertThat(bookCodec.read(accessFile)).isEqualTo(bookD);
    }

}