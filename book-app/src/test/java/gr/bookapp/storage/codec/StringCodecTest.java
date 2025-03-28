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
    void initialize(@TempDir Path dir) throws IOException {
        stringCodec = new StringCodec();
        accessFile = new RandomAccessFile(dir.resolve("longCodec.data").toFile(), "rw");
        accessFile.setLength(1000);
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

    @Test
    @DisplayName("Multiple Write-Read test")
    void multipleWriteReadTest() throws IOException {
        String input = "Beef";
        String secondInput = "Chicken";
        stringCodec.write(accessFile, input);
        stringCodec.write(accessFile, secondInput);
        accessFile.seek(0);
        String value = stringCodec.read(accessFile);
        String secondValue = stringCodec.read(accessFile);
        assertThat(value).isEqualTo(input);
        assertThat(secondValue).isEqualTo(secondInput);
    }

}