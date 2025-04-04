package gr.bookapp.storage.codec;

import gr.bookapp.protocol.codec.StringCodec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class TreeCodecTest {

    TreeCodec<String, String> treeCodec;
    StringCodec stringCodec;
    @TempDir Path dir;
    RandomAccessFile accessFile;

    @BeforeEach
    void initialize() throws IOException {
        accessFile = new RandomAccessFile(dir.resolve("treeCodecTest.data").toFile(),"rw");
        accessFile.setLength(1000);
        stringCodec = new StringCodec();
        treeCodec = new TreeCodec<>(stringCodec, stringCodec);
    }

    @Test
    @DisplayName("Max byte size test")
    void maxByteSizeTest() {
        assertThat(treeCodec.maxByteSize()).isEqualTo(stringCodec.maxByteSize() * 2 + Long.BYTES * 2);
    }

    @Test
    @DisplayName("Write-Read test")
    void writeReadTest() throws IOException {
        String key = "1";
        String value = "Beef";
        treeCodec.write(accessFile, key, value);

        System.out.println(accessFile.getFilePointer());
        accessFile.seek(0);
        TreeNodeDual<String, String> node = treeCodec.read(accessFile);

        assertThat(node.key()).isEqualTo(key);
        assertThat(node.value()).isEqualTo(value);
    }
}