package gr.bookapp.storage.codec;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ListCodecTest {

    @TempDir
    Path dir;
    RandomAccessFile accessFile;
    ListCodec<String> listCodec;

    @BeforeEach
    void initialize() throws IOException {
        listCodec = new ListCodec<>(new StringCodec(10), 5);
        accessFile = new RandomAccessFile(dir.resolve("EmployeeCodec.data").toFile(), "rw");
        accessFile.setLength(1000);
    }

    @Test
    @DisplayName("Write-Read single value test")
    void writeReadSingleValueTest() throws IOException {
        List<String> list = List.of("example1");
        listCodec.write(accessFile, list);
        accessFile.seek(0);
        assertThat(listCodec.read(accessFile)).isEqualTo(list);
    }

    @Test
    @DisplayName("Write-Read multiple values test")
    void writeReadMultipleValuesTest() throws IOException {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(String.valueOf(i));
        }
        listCodec.write(accessFile, list);
        accessFile.seek(0);
        assertThat(listCodec.read(accessFile)).isEqualTo(list);
    }

    @Test
    @DisplayName("Write-Read with entries that specified in constructor")
    void writeReadWithEntriesThatSpecifiedInConstructor() throws IOException {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            list.add(String.valueOf(i));
        }
        assertThrows(IllegalStateException.class, () -> listCodec.write(accessFile, list));
    }


}