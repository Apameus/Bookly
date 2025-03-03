package gr.bookapp.storage.codec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class StringCodecTest {

    @TempDir Path dir;
    RandomAccessFile accessFile;
    StringCodec stringCodec;

    @BeforeEach
    void initialize(@TempDir Path dir) throws FileNotFoundException {
        stringCodec = new StringCodec();
        accessFile = new RandomAccessFile(dir.resolve("stringCodec.data").toFile(), "rw");
    }

    @Test
    @DisplayName("Write-Read test")
    void writeReadTest() throws IOException {
        String input = "Beef";
        stringCodec.write(accessFile, input);
        accessFile.seek(0);
        String value = stringCodec.read(accessFile);
        assertThat(value).isEqualTo(input);
    }

}