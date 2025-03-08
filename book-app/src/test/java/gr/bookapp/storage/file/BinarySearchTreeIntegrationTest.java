package gr.bookapp.storage.file;

import gr.bookapp.storage.codec.IntegerCodec;
import gr.bookapp.storage.codec.StringCodec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Path;
import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

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


    @Test
    @DisplayName("Overload acceding insertion-deletion test")
    void overloadTest() throws IOException {
        NodeStorageTree<Integer, String> nodeStorageTree = new FileBasedNodeStorageTree<>(dir.resolve("fff.test"), new IntegerCodec(), stringCodec);
        BinarySearchTree<Integer, String> bst = new BinarySearchTree<>(Integer::compareTo, nodeStorageTree);
        for (int i = 0; i < 100; i++) {
            bst.insert(i, valueOf(i));
        }
        for (int i = 0; i < 100; i++) {
            assertThat(bst.retrieve(i)).isEqualTo(valueOf(i));
            bst.delete(i);
        }
        assertThat(binarySearchTree.retrieve("0")).isNull();
    }

    @Test
    @DisplayName("Overload descending insertion-deletion test")
    void overloadTestII() throws IOException {
        NodeStorageTree<Integer, String> nodeStorageTree = new FileBasedNodeStorageTree<>(dir.resolve("fff.test"), new IntegerCodec(), stringCodec);
        BinarySearchTree<Integer, String> bst = new BinarySearchTree<>(Integer::compareTo, nodeStorageTree);
        for (int i = 100; i > 0; i--) {
            bst.insert(i, valueOf(i));
        }
        for (int i = 100; i > 0; i--) {
            assertThat(bst.retrieve(i)).isEqualTo(valueOf(i));
            bst.delete(i);
        }
        assertThat(binarySearchTree.retrieve("0")).isNull();
    }

    @Test
    @DisplayName("Overload Test")
    void overloadTestIII() throws IOException {
        for (int i = 0; i < 1000; i++) {
            binarySearchTree.insert(valueOf(i), valueOf(i));
        }
        for (int i = 0; i < 1000; i++) {
            assertThat(binarySearchTree.retrieve(valueOf(i))).isEqualTo(valueOf(i));
            binarySearchTree.delete(valueOf(i));
        }
        assertThat(binarySearchTree.retrieve("0")).isNull();
        assertThat(binarySearchTree.retrieve("9")).isNull();
    }

    @Test
    @DisplayName("Delete root")
    void deleteRoot() throws IOException {
        NodeStorageTree<Integer, String> nodeStorageTree = new FileBasedNodeStorageTree<>(dir.resolve("fff.test"), new IntegerCodec(), stringCodec);
        BinarySearchTree<Integer, String> bst = new BinarySearchTree<>(Integer::compareTo, nodeStorageTree);

        bst.insert(10, "10");
        bst.insert(5, "5");
        bst.insert(12, "12");
        bst.insert(11, "11");

        bst.delete(10);
        assertThat(bst.retrieve(10)).isNull();
        assertThat(bst.retrieve(5)).isEqualTo("5");
        assertThat(bst.retrieve(12)).isEqualTo("12");
        assertThat(bst.retrieve(11)).isEqualTo("11");
    }

    @Test
    @DisplayName("Chain insertion-deletion test")
    void chainInsertionDeletionTest() throws IOException {
        NodeStorageTree<Integer, String> nodeStorageTree = new FileBasedNodeStorageTree<>(dir.resolve("fff.test"), new IntegerCodec(), stringCodec);
        BinarySearchTree<Integer, String> bst = new BinarySearchTree<>(Integer::compareTo, nodeStorageTree);

        bst.insert(10, "10");
        bst.insert(5,"5");
        bst.insert(15,"15");
        bst.insert(7,"7");
        bst.insert(3, "3");
        bst.insert(17,"17");
        bst.insert(13,"13");
        bst.insert(20, "20");
        //      10
        //   5      15
        // 3  7   13  17
        //              20
        bst.delete(10);
        bst.delete(13);
        bst.delete(15);
        bst.delete(17);
        bst.delete(3);
    }

}