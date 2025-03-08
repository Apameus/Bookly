package gr.bookapp.storage.file;

import gr.bookapp.storage.codec.Codec;
import gr.bookapp.storage.codec.TreeCodec;
import gr.bookapp.storage.codec.TreeNode;
import gr.bookapp.storage.codec.TreeNodeDual;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public final class FileBasedNodeStorageTree<K, V> implements NodeStorageTree<K, V> {

    private final RandomAccessFile accessFile;
    private final TreeCodec<K,V> treeCodec;
    private final Codec<K> keyCodec;
    private final Codec<V> valueCodec;

    private static final byte FLAG_SIZE = 1;
    private static final byte EXIST_FLAG = 1;
    private static final byte NULL_FLAG = 0;
    private static final byte CHILD_REFERENCE_SIZE = 8;
    private static final byte STORED_ENTRIES_SIZE = 4;
    private final int maxSizeOfEntry;

    private int storedEntries;
    private int availableEntries;

    public FileBasedNodeStorageTree(Path path, Codec<K> keyCodec, Codec<V> valueCodec) throws IOException {
        accessFile = new RandomAccessFile(path.toFile(), "rw");
        treeCodec = new TreeCodec<K,V>(keyCodec, valueCodec);
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;

        maxSizeOfEntry = FLAG_SIZE + treeCodec.maxByteSize();
        if (accessFile.length() == 0) {
            availableEntries = 16;
            accessFile.setLength((long) maxSizeOfEntry * availableEntries + STORED_ENTRIES_SIZE);
        }
        else {
            accessFile.seek(0);
            storedEntries = accessFile.readInt();
            availableEntries = (int) ( (accessFile.length() - STORED_ENTRIES_SIZE) / maxSizeOfEntry);
        }
    }

    @Override
    public long rootOffset() {
        return STORED_ENTRIES_SIZE;
    }

    @Override
    public boolean isNull(long nodeOffset) {
        try {
            accessFile.seek(nodeOffset);
            return accessFile.read() == NULL_FLAG;
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    @Override
    public void updatePointer(long parentOffset, String childSide, long childPointer) {
        if (parentOffset == 0 || childSide.isEmpty()) return;
        try {
            if (childSide.equalsIgnoreCase("L")){
                accessFile.seek(parentOffset + FLAG_SIZE + keyCodec.maxByteSize());
                accessFile.writeLong(childPointer);
            }
            else if (childSide.equalsIgnoreCase("R")){
                accessFile.seek(parentOffset + FLAG_SIZE + keyCodec.maxByteSize() + CHILD_REFERENCE_SIZE);
                accessFile.writeLong(childPointer);
            }
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    @Override
    public TreeNode<K> readKeyNode(long nodeOffset) {
        try {
            accessFile.seek(nodeOffset + FLAG_SIZE);
            return treeCodec.readKeyEntry(accessFile);
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    @Override
    public V readValue(long nodeOffset){
        try {
            accessFile.seek(nodeOffset + FLAG_SIZE + treeCodec.keyByteSize());
            return valueCodec.read(accessFile);
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    @Override
    public void writeNode(TreeNodeDual<K, V> node, long offset) {
        try {
            if (isFull()) resize();
            accessFile.seek(offset);
            accessFile.write(EXIST_FLAG);
            treeCodec.write(accessFile, node);
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    @Override
    public void updateNode(K key, V value, long offset) {
        try {
            accessFile.seek(offset + FLAG_SIZE);
            keyCodec.write(accessFile, key);
            accessFile.skipBytes(CHILD_REFERENCE_SIZE * 2);
            valueCodec.write(accessFile, value);
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    @Override
    public void deleteNode(long nodeOffset) {
        try {
            accessFile.seek(nodeOffset);
            accessFile.write(NULL_FLAG);
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    @Override
    public void updateStoredEntries(int by) {
        try {
            accessFile.seek(0);
            accessFile.writeInt(storedEntries += by);
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    @Override
    public long findEmptySlot() {
        long offset = STORED_ENTRIES_SIZE;
        try {
            accessFile.seek(offset);
            while (accessFile.read() == EXIST_FLAG){
                offset += maxSizeOfEntry;
                accessFile.seek(offset);
            }
        } catch (IOException e) {throw new RuntimeException(e);}
        return offset;
    }


    public boolean isFull() {
        return storedEntries == availableEntries;
    }

    public void resize() {
        availableEntries *= 2;
        try {
            accessFile.setLength((long) availableEntries * maxSizeOfEntry + STORED_ENTRIES_SIZE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
