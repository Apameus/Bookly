package gr.bookapp.storage.file;

import gr.bookapp.storage.codec.StringCodec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BinarySearchTreeIntegrationTest {

    @TempDir Path dir;
    StringCodec stringCodec;
    NodeStorageTree<String, String> nodeStorage;
    BinarySearchTree<String, String> binarySearchTree;

    @BeforeEach
    void initialize() throws IOException {
        stringCodec = new StringCodec();
        nodeStorage = new FileBasedNodeStorageTree<>(dir.resolve("BstIntegration.test"), stringCodec, stringCodec);
        binarySearchTree = new BinarySearchTree<>(String::compareTo, nodeStorage);
    }

    @Test
    @DisplayName("Retrieve from null root")
    void retrieveFromNullRoot() {
        assertThat(binarySearchTree.retrieve("A1")).isNull();
    }

    @Test
    @DisplayName("Insert-Retrieve")
    void insertRetrieve() {
        String key = "A1";
        String value = "A";
        binarySearchTree.insert(key, value);
        assertThat(binarySearchTree.retrieve(key)).isEqualTo(value);
    }

    @Test
    @DisplayName("Insert-Retrieve with Collision")
    void insertRetrieveWithCollision() {
        String keyA = "A1";
        String valueA = "A";
        String keyB = "B2";
        String valueB = "B";
        binarySearchTree.insert(keyA, valueA);
        binarySearchTree.insert(keyB, valueB);
        assertThat(binarySearchTree.retrieve(keyA)).isEqualTo(valueA);
        assertThat(binarySearchTree.retrieve(keyB)).isEqualTo(valueB);
    }

    @Test
    @DisplayName("Insert with already existing key")
    void insertWithAlreadyExistingKey() {
        String key = "A1";
        String valueA = "A";
        String valueB = "B";
        binarySearchTree.insert(key, valueA);
        assertThat(binarySearchTree.retrieve(key)).isEqualTo(valueA);
        binarySearchTree.insert(key, valueB);
        assertThat(binarySearchTree.retrieve(key)).isEqualTo(valueB);
    }

    @Test
    @DisplayName("Deletion test")
    void deletionTest() {
        String key = "A1";
        String value = "A";
        binarySearchTree.insert(key, value);
        binarySearchTree.delete(key);
        assertThat(binarySearchTree.retrieve(key)).isNull();
    }

    @Test
    @DisplayName("Deletion from collision")
    void deletionFromCollision() {
        String keyA = "A1";
        String valueA = "A";
        String keyB = "B2";
        String valueB = "B";
        binarySearchTree.insert(keyA, valueA);
        binarySearchTree.insert(keyB, valueB);
        binarySearchTree.delete(keyB);
        assertThat(binarySearchTree.retrieve(keyA)).isEqualTo(valueA);
        assertThat(binarySearchTree.retrieve(keyB)).isNull();
    }

}