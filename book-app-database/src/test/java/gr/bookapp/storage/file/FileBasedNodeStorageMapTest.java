package gr.bookapp.storage.file;

import gr.bookapp.protocol.codec.StringCodec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FileBasedNodeStorageMapTest {

    @TempDir Path dir;
    StringCodec codec;
    FileBasedNodeStorageMap<String, String> nodeStorage;
    int maxSizeOfEntry;

    @BeforeEach
    void initialize() throws IOException {
        codec = new StringCodec();
        nodeStorage = new FileBasedNodeStorageMap<>(dir.resolve("NodeStorageMap.test"), codec, codec);
        maxSizeOfEntry = Byte.BYTES + Long.BYTES + codec.maxByteSize() * 2;
    }


    private long entry(int num){
        return ((long) maxSizeOfEntry * num) + Integer.BYTES;
    }

    @Test
    @DisplayName("Write-Read Test")
    void writeReadTest() {
        nodeStorage.writeNode("A", "Manolis", entry(1));

        assertThat(nodeStorage.readValue(entry(1))).isEqualTo("Manolis");
    }

    @Test
    @DisplayName("Update pointer test")
    void updatePointerTest() {
        nodeStorage.writeNode( "A","Manolis", entry(1));
        nodeStorage.updateNextOffset(entry(1), entry(2));

        assertThat(nodeStorage.readNextOffset(entry(1))).isEqualTo(entry(2));
    }

    @Test
    @DisplayName("Delete node test")
    void deleteNodeTest() {
        nodeStorage.writeNode( "A","Manolis", entry(1));
        nodeStorage.deleteNode(entry(1));
        assertThat(nodeStorage.isNull(entry(1))).isTrue();
    }

    @Test
    @DisplayName("Resize test")
    void resizeTest() {
        for (int i = 0; i < 16; i++) {
            nodeStorage.writeNode(String.valueOf(i), String.valueOf(i), entry(i));
            nodeStorage.updateStoredEntries(1);
        }
        HashMap<String, String> entriesBeforeResizing = nodeStorage.resize();
        assertThat(entriesBeforeResizing.size()).isEqualTo(16);

        for (int i = 0; i < 16; i++) {
            assertThat(entriesBeforeResizing.get(String.valueOf(i))).isEqualTo(String.valueOf(i));
            assertThat(nodeStorage.isNull(entry(i))).isTrue();
        }
    }

    @Test
    @DisplayName("Entry Iterator test")
    void entryIteratorTest() {
        for (int i = 0; i < 5; i++) {
            nodeStorage.writeNode(String.valueOf(i), String.valueOf(i), entry(i));
        }
        Iterator<Map.Entry<String, String>> entryIterator = nodeStorage.entriesIterator();
        for (int i = 0; i < 5; i++) {
            assertThat(entryIterator.hasNext()).isTrue();
            assertThat(entryIterator.next()).isEqualTo(Map.entry(String.valueOf(i), String.valueOf(i)));
        }
        assertThat(entryIterator.hasNext()).isFalse();
    }

}