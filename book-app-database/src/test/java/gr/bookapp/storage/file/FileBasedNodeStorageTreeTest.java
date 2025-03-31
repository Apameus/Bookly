package gr.bookapp.storage.file;

import gr.bookapp.storage.codec.StringCodec;
import gr.bookapp.storage.codec.TreeNodeDual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FileBasedNodeStorageTreeTest {

    @TempDir Path dir;
    StringCodec keyCodec;
    StringCodec valueCodec;
    FileBasedNodeStorageTree<String, String> fileBasedNodeStorage;
    int maxSizeOfEntry;

    @BeforeEach
    void initialize() throws IOException {
        keyCodec = new StringCodec();
        valueCodec = new StringCodec();
        fileBasedNodeStorage = new FileBasedNodeStorageTree<>(dir.resolve("FileBasedNodeStorageTest"), keyCodec, valueCodec);
        maxSizeOfEntry = Byte.BYTES + keyCodec.maxByteSize() + Long.BYTES * 2 + valueCodec.maxByteSize();
    }

    private long entry(int num){
        return ((long) maxSizeOfEntry * num) + Integer.BYTES;
    }


    @Test
    @DisplayName("Write-Read Test")
    void writeReadTest() {
        var node = new TreeNodeDual<String, String>("A","Manolis");
        fileBasedNodeStorage.writeNode(node, entry(1));

        var keyNode = fileBasedNodeStorage.readKeyNode(entry(1));
        var value = fileBasedNodeStorage.readValue(entry(1));
        var foundNode = new TreeNodeDual<String, String>(keyNode, value);

        assertThat(node).isEqualTo(foundNode);
    }

    @Test
    @DisplayName("Update pointer test")
    void updatePointerTest() {
        var node = new TreeNodeDual<String, String>("A","Manolis");
        fileBasedNodeStorage.writeNode(node, entry(1));
        fileBasedNodeStorage.updatePointer(entry(1), "L", entry(2));

        var keyNode = fileBasedNodeStorage.readKeyNode(entry(1));
        var value = fileBasedNodeStorage.readValue(entry(1));
        var foundNode = new TreeNodeDual<String, String>(keyNode, value);

        assertThat(foundNode.leftPointer()).isEqualTo(entry(2));
    }

    @Test
    @DisplayName("Delete node test")
    void deleteNodeTest() {
        fileBasedNodeStorage.writeNode(new TreeNodeDual<>("A","Manolis"), entry(1));
        fileBasedNodeStorage.deleteNode(entry(1));
        assertThat(fileBasedNodeStorage.isNull(entry(1))).isTrue();
    }

    @Test
    @DisplayName("Iterative find empty slot test")
    void test() {
        for (int i = 0; i < 5; i++) {
            fileBasedNodeStorage.writeNode(new TreeNodeDual<>("i", "i"), entry(i));
        }
        assertThat(fileBasedNodeStorage.findEmptySlot()).isEqualTo(entry(5));
    }

    @Test
    @DisplayName("Entry Iterator test")
    void entryIteratorTest() {
        for (int i = 0; i < 5; i++) {
            fileBasedNodeStorage.writeNode(new TreeNodeDual<>(String.valueOf(i), String.valueOf(i)), entry(i));
            fileBasedNodeStorage.updateStoredEntries(1);
        }
        Iterator<Map.Entry<String, String>> entryIterator = fileBasedNodeStorage.entriesIterator();
        for (int i = 0; i < 5; i++) {
            assertThat(entryIterator.hasNext()).isTrue();
            assertThat(entryIterator.next()).isEqualTo(Map.entry(String.valueOf(i), String.valueOf(i)));
        }
        assertThat(entryIterator.hasNext()).isFalse();
    }
}