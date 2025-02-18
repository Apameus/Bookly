package codec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.*;


class DateCodecTest {

    DateCodec dateCodec;

    @BeforeEach
    void initialize(){
        dateCodec = new DateCodec();
    }

    @Test
    @DisplayName("test")
    void test(@TempDir Path dir) throws IOException {
        RandomAccessFile accessFile = new RandomAccessFile(dir.resolve("test.data").toFile(), "rw");
        dateCodec.write(accessFile, LocalDate.parse("18/02/2025", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        LocalDate actualDate = dateCodec.read(accessFile);
        assertThat(actualDate).isEqualTo("18/02/2025");
    }
}