package gr.bookapp.storage.file;

import gr.bookapp.storage.codec.StringCodec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Path;

import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

class HashMapIntegrationTest {

    @TempDir
    Path dir;
    StringCodec stringCodec;
    NodeStorageMap<String, String> nodeStorageMap;
    HashMap<String, String> hashMap;

    @BeforeEach
    void initialize() throws IOException {
        stringCodec = new StringCodec();
        nodeStorageMap = new FileBasedNodeStorageMap<>(dir.resolve("HashMapIntegration.test"), stringCodec, stringCodec);
        hashMap = new HashMap<>(String::compareTo, nodeStorageMap);
    }

    @Test
    @DisplayName("Retrieve with non-existing key")
    void retrieveWithNonExistingKey() {
        assertThat(hashMap.retrieve("A")).isEqualTo(null);
    }

    @Test
    @DisplayName("Insert-Retrieve test")
    void insertRetrieveTest() {
        String key = "A1";
        String value = "A";
        hashMap.insert(key, value);
        assertThat(hashMap.retrieve(key)).isEqualTo(value);
    }

    @Test
    @DisplayName("Insert-Retrieve from Collision")
    void insertRetrieveFromCollision() {
        long offsetA = 25L;
        String keyA = "A";
        String valueA = "A1";

        long offsetB = 50L;
        String keyB = "B2";
        String valueB = "B";

        hashMap.insert(keyA, valueA);
        hashMap.insert(keyB, valueB);

        assertThat(hashMap.retrieve(keyA)).isEqualTo(valueA);
        assertThat(hashMap.retrieve(keyB)).isEqualTo(valueB);
    }

    @Test
    @DisplayName("Insert with already existing key")
    void insertWithAlreadyExistingKey() {
        String key = "A1";
        String valueA = "A";
        String valueB = "B";

        hashMap.insert(key,valueA);
        assertThat(hashMap.retrieve(key)).isEqualTo(valueA);
        hashMap.insert(key,valueB);
        assertThat(hashMap.retrieve(key)).isEqualTo(valueB);
    }

    @Test
    @DisplayName("Deletion Test")
    void deletionTest() {
        String key = "A1";
        String value = "A";
        hashMap.insert(key, value);
        hashMap.delete(key);
        assertThat(hashMap.retrieve(key)).isNull();
    }

    @Test
    @DisplayName("Deletion from collision")
    void deletionFromCollision() {
        long offsetA = 25L;
        String keyA = "A";
        String valueA = "A1";

        long offsetB = 50L;
        String keyB = "B2";
        String valueB = "B";

        hashMap.insert(keyA, valueA);
        hashMap.insert(keyB, valueB);

        hashMap.delete(keyB);
        assertThat(hashMap.retrieve(keyA)).isEqualTo(valueA);
        assertThat(hashMap.retrieve(keyB)).isNull();
    }

    @Test
    @DisplayName("Overload insert & retrieve")
    void overloadInsertRetrieve() {
        for (int i = 0; i < 30; i++) {
            hashMap.insert(valueOf(i), valueOf(i));
        }
        for (int i = 0; i < 30; i++) {
            assertThat(hashMap.retrieve(valueOf(i))).isEqualTo(valueOf(i));
        }
    }
}