package gr.bookapp.storage.codec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import static gr.bookapp.common.InstantFormatter.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InstantCodecTest {
    @TempDir Path dir;
    RandomAccessFile accessFile;
    StringCodec stringCodec;
    LongCodec longCodec;
    InstantCodec instantCodec;

    @BeforeEach
    void initialize() throws FileNotFoundException {
        longCodec = new LongCodec();
        stringCodec = new StringCodec();
        instantCodec = new InstantCodec(longCodec);
        accessFile = new RandomAccessFile(dir.resolve("InstantCodec.data").toFile(), "rw");
    }

    @Test
    @DisplayName("Write-Read with BC date test")
    void writeReadWithBcDateTest() throws IOException {
        String date = "22-02-0300 BC";
        Instant instant = parse(date);
        instantCodec.write(accessFile, instant);
        accessFile.seek(0);
        assertThat(instantCodec.read(accessFile)).isEqualTo(instant);
    }

    @Test
    @DisplayName("Write-Read with AD date")
    void writeReadWithAdDate() throws IOException {
        String date = "22-02-2025 AD";
        Instant instant = parse(date);
        instantCodec.write(accessFile, instant);
        accessFile.seek(0);
        assertThat(instantCodec.read(accessFile)).isEqualTo(instant);
    }

    @Test
    @DisplayName("Write-Read invalid date")
    void writeReadInvalidDate() {
        String invalidDate = "01-25-2000";
        assertThrows(DateTimeParseException.class, () -> parse(invalidDate));
    }

}