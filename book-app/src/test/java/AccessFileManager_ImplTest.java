import accessFile.AccessFileManager_Impl;
import accessFile.codec.LongCodec;
import accessFile.codec.StringCodec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class AccessFileManager_ImplTest {

    AccessFileManager_Impl<Long, String> accessFileManager;

    @BeforeEach
    void initialize(@TempDir Path dir) throws IOException {
        accessFileManager = new AccessFileManager_Impl<>(dir.resolve("AccessFileManagerTest.data"), new LongCodec(), new StringCodec(), Long::compareTo);
    }

    @Test
    @DisplayName("Insert to an empty file")
    void insertToAnEmptyFile() throws IOException {
        accessFileManager.insert(15L, "Manolis");

        assertThat(accessFileManager.get(15L)).isEqualTo("Manolis");
        assertThat(accessFileManager.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Multiple Insertion")
    void multipleInsertion() throws IOException {
        accessFileManager.insert(17L, "532");
        accessFileManager.insert(15L, "452");
        accessFileManager.insert(16L, "492");
        accessFileManager.insert(18L, "572");

        assertThat(accessFileManager.get(18L)).isEqualTo("572");
        assertThat(accessFileManager.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("Delete root")
    void deleteRoot() throws IOException {
        accessFileManager.insert(15L, "452");
        accessFileManager.delete(15L);

        assertThat(accessFileManager.get(15L)).isNull();
        assertThat(accessFileManager.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Multiple Deletion")
    void multipleDeletion() throws IOException {
        accessFileManager.insert(15L, "452");
        accessFileManager.insert(20L, "572");
        accessFileManager.insert(17L, "532");
        accessFileManager.insert(18L, "672");
        accessFileManager.insert(16L, "492");
        accessFileManager.insert(21L, "772");

        assertThat(accessFileManager.size()).isEqualTo(6);

        accessFileManager.delete(15L);

        assertThat(accessFileManager.get(20L)).isEqualTo("572");
        assertThat(accessFileManager.get(21L)).isEqualTo("772");
        assertThat(accessFileManager.size()).isEqualTo(5);

        accessFileManager.delete(17L);
        accessFileManager.delete(16L);

        assertThat(accessFileManager.get(18L)).isEqualTo("672");
        assertThat(accessFileManager.size()).isEqualTo(3);
    }


    @Test
    @DisplayName("Overload Test")
    void overloadTest() throws IOException {
        for (long i = 0; i < 500; i++) {
            accessFileManager.insert(i, String.valueOf(i * 10));
        }
        assertThat(accessFileManager.size()).isEqualTo(500);

        for (long i = 0; i < 500; i++) {
            assertThat(accessFileManager.get(i)).isEqualTo(String.valueOf(i * 10));
            accessFileManager.delete(i);
            assertThat(accessFileManager.size()).isEqualTo(499 - i);
        }
    }
}

