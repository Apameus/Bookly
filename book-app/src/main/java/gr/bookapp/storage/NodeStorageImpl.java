package gr.bookapp.storage;

import gr.bookapp.storage.codec.Codec;
import gr.bookapp.storage.codec.TreeCodec;
import gr.bookapp.storage.codec.TreeNode;
import gr.bookapp.storage.codec.TreeNodeDual;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Comparator;

public final class NodeStorageImpl<K, V> implements NodeStorage<K, V> {

    private final RandomAccessFile accessFile;
    private final TreeCodec<K,V> treeCodec;
    private final Codec<K> keyCodec;
    private final Codec<V> valueCodec;
    private final Comparator<K> comparator;

    private final byte FLAG_SIZE = 1;
    private final byte EXIST_FLAG = 1;
    private final byte NULL_FLAG = 0;
    private final byte CHILD_REFERENCE_SIZE = 8;
    private final byte STORED_ENTRIES_SIZE = 4;
    private final int maxSizeOfEntry;

    private int storedEntries;
    private int availableEntries;

    public NodeStorageImpl(Path path, Codec<K> keyCodec, Codec<V> valueCodec, Comparator<K> comparator) throws IOException {
        accessFile = new RandomAccessFile(path.toFile(), "rw");
        treeCodec = new TreeCodec<K,V>(keyCodec, valueCodec);
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
        this.comparator = comparator;

        maxSizeOfEntry = FLAG_SIZE + treeCodec.maxByteSize();
        if (accessFile.length() == 0) {
            availableEntries = 16;
            accessFile.setLength((long) maxSizeOfEntry * availableEntries + STORED_ENTRIES_SIZE);
        }
        else { availableEntries = (int) (accessFile.length() / maxSizeOfEntry); }
    }


    @Override
    public long rootOffset() {
        return STORED_ENTRIES_SIZE;
    }

    @Override
    public boolean isNull(long nodeOffset) throws IOException {
        accessFile.seek(nodeOffset);
        return accessFile.read() == NULL_FLAG;
    }

    @Override
    public void updatePointer(long nodeOffset, long pointer, String leftOrRight) throws IOException {
        if (leftOrRight.equalsIgnoreCase("L")){
            accessFile.seek(nodeOffset + FLAG_SIZE + keyCodec.maxByteSize());
            accessFile.writeLong(pointer);
        }
        else if (leftOrRight.equalsIgnoreCase("R")){
            accessFile.seek(nodeOffset + FLAG_SIZE + keyCodec.maxByteSize() + CHILD_REFERENCE_SIZE);
            accessFile.writeLong(pointer);
        }
    }

    @Override
    public TreeNode<K> readKeyNode(long nodeOffset) throws IOException {
        return treeCodec.readKeyEntry(accessFile);
    }

    @Override
    public V readValue(long nodeOffset) throws IOException {
        accessFile.seek(nodeOffset + FLAG_SIZE+ treeCodec.halfByteSize());
        return valueCodec.read(accessFile);
    }

    @Override
    public void writeNode(TreeNodeDual<K, V> node, long offset) throws IOException {
        accessFile.seek(offset);
        treeCodec.write(accessFile, node);
    }

    @Override
    public void updateNode(K key, V value, long offset) throws IOException {
        accessFile.seek(offset + FLAG_SIZE);
        keyCodec.write(accessFile, key);
        accessFile.skipBytes(CHILD_REFERENCE_SIZE * 2);
        valueCodec.write(accessFile, value);
    }

    @Override
    public void deleteNode(long nodeOffset) throws IOException {
        accessFile.seek(nodeOffset);
        accessFile.write(NULL_FLAG);
    }

    @Override
    public void updateStoredEntries(int by) throws IOException {
        accessFile.seek(0);
        accessFile.writeInt(storedEntries += by);
    }

    @Override
    public long findEmptySlot() throws IOException {
        long offset = STORED_ENTRIES_SIZE;
        accessFile.seek(offset);
        while (accessFile.read() == EXIST_FLAG){
            offset += maxSizeOfEntry;
            accessFile.seek(offset);
        }
        return offset;
    }
}
