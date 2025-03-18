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
import static java.time.LocalDate.ofInstant;
import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.assertj.core.api.Assertions.assertThat;

class InstantCodecTest {
    @TempDir Path dir;
    RandomAccessFile accessFile;
    StringCodec stringCodec;
    InstantCodec instantCodec;

    @BeforeEach
    void initialize() throws FileNotFoundException {
        stringCodec = new StringCodec();
        instantCodec = new InstantCodec(stringCodec);
        accessFile = new RandomAccessFile(dir.resolve("InstantCodec.data").toFile(), "rw");
    }

    @Test
    @DisplayName("Write-Read with BC date test")
    void writeReadWithBcDateTest() throws IOException {
        String date = "22-02-0300 BC"; //
        Instant instant = InstantFormatter.parse(date);
        instantCodec.write(accessFile, instant);
        accessFile.seek(0);
        assertThat(ofInstant(instantCodec.read(accessFile), UTC).format(ofPattern("dd-MM-yyyy G"))).isEqualTo("22-02-0300 BC");
    }

    @Test
    @DisplayName("Write-Read with AD date")
    void writeReadWithAdDate() throws IOException {
        String date = "22-02-2025 AD"; //
        Instant instant = InstantFormatter.parse(date);
        instantCodec.write(accessFile, instant);
        accessFile.seek(0);
        assertThat(instantCodec.read(accessFile)).isEqualTo(InstantFormatter.parse(date));
    }

}