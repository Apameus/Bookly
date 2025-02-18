import codec.Codec;
import codec.TreeCodec;
import codec.TreeNode_DualValue_Pointers;
import codec.TreeNode_Pointers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Comparator;

public final class AccessFileManager_Impl<K,V> implements AccessFIleManager<K,V>{
    private final RandomAccessFile accessFile;
    private final TreeCodec<K> treeCodec;
    private final Codec<K> keyCodec;
    private final Codec<V> valueCodec;
    private final Comparator<K> comparator;

    private final byte EXIST_FLAG = 1;
    private final byte FLAG_SIZE = 1;
    private final byte CHILD_REFERENCE_SIZE = 8;
    private final byte STORED_ENTRIES_SIZE = 4;
    private final int maxSizeOfEntry;

    private int storedEntries;
    private int availableEntries;


    public AccessFileManager_Impl(Path path, Codec<K> keyCodec, Codec<V> valueCodec, Comparator<K> comparator, int availableEntries) throws IOException {
        accessFile = new RandomAccessFile(path.toFile(), "rw");
        treeCodec = new TreeCodec<>(keyCodec);
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
        this.comparator = comparator;
        this.availableEntries = availableEntries;

        maxSizeOfEntry = FLAG_SIZE + treeCodec.maxByteSize() + valueCodec.maxByteSize();
        if (accessFile.length() == 0) accessFile.setLength((long) maxSizeOfEntry * availableEntries + STORED_ENTRIES_SIZE);
    }
    public AccessFileManager_Impl(Path path, Codec<K> keyCodec, Codec<V> valueCodec, Comparator<K> comparator) throws IOException {
        this(path,keyCodec,valueCodec, comparator,16);
    }

    @Override
    public void insert(K key, V value) throws IOException {
        insert(key, value, STORED_ENTRIES_SIZE);
    }
    private void insert(K key, V value, long offset) throws IOException {
        if (offset == 0) return;
        accessFile.seek(offset);
        while (accessFile.read() == EXIST_FLAG){
            TreeNode_Pointers<K> found = treeCodec.read(accessFile);
            if (comparator.compare(key, found.data) < 0){
                if (found.leftChild == 0){
                    long leftPointer = updatePointer(offset, "L");
                    insert(key, value, leftPointer);
                    return;
                }
                accessFile.seek(found.leftChild);
            }
            else if (comparator.compare(key, found.data) > 0) {
                if (found.rightChild == 0){
                    long rightPointer = updatePointer(offset, "R");
                    insert(key, value, rightPointer);
                    return;
                }
                accessFile.seek(found.rightChild);
            }
        }
        accessFile.seek(offset);
        accessFile.write(EXIST_FLAG);
        treeCodec.write(accessFile, new TreeNode_Pointers<>(key));
        valueCodec.write(accessFile, value);
        updateStoredEntries(++storedEntries);
    }

    @Override
    public V get(K key) throws IOException {
        return get(key, STORED_ENTRIES_SIZE);
    }
    private V get(K key, long offset) throws IOException {
        if (offset == 0) return null;
        accessFile.seek(offset + FLAG_SIZE);
        TreeNode_Pointers<K> node = treeCodec.read(accessFile);
        if (comparator.compare(key, node.data) < 0) return get(key, node.leftChild);
        else if (comparator.compare(key, node.data) > 0) return get(key, node.rightChild);
        else return valueCodec.read(accessFile);
    }

    @Override
    public void delete(K key) throws IOException {
        delete(key, STORED_ENTRIES_SIZE, 0, "");
    }
    private void delete(K key, long offset, long prevOffset, String prevLeftOrRight) throws IOException {
        if (offset == 0) return;
        accessFile.seek(offset + FLAG_SIZE);
        TreeNode_Pointers<K> node = treeCodec.read(accessFile);
        if (comparator.compare(key, node.data) < 0) delete(key, node.leftChild, offset, "L");
        else if (comparator.compare(key, node.data) > 0) delete(key, node.rightChild, offset, "R");
        else {
            if (node.leftChild == 0){
                if (node.rightChild != 0){
                    accessFile.seek(node.rightChild + FLAG_SIZE);
                    var rightChild = treeCodec.read(accessFile);
                    V value = valueCodec.read(accessFile);
                    accessFile.seek(offset + FLAG_SIZE);
                    treeCodec.write(accessFile, rightChild);
                    valueCodec.write(accessFile, value);
                }
                else {
                    accessFile.seek(offset);
                    accessFile.write(0);
                    updatePointer(prevOffset, 0, prevLeftOrRight);
                }
                updateStoredEntries(--storedEntries);
            }
            else if (node.rightChild == 0){
                accessFile.seek(node.leftChild);
                var leftChild = treeCodec.read(accessFile);
                V value = valueCodec.read(accessFile);
                accessFile.seek(offset + FLAG_SIZE);
                treeCodec.write(accessFile, leftChild);
                valueCodec.write(accessFile, value);
                updateStoredEntries(--storedEntries);
            }
            else {
                var successor = leftMost(node.rightChild);
                accessFile.seek(offset + FLAG_SIZE);
                keyCodec.write(accessFile, successor.key);
                accessFile.skipBytes(CHILD_REFERENCE_SIZE * 2);
                valueCodec.write(accessFile, successor.value);
                updateStoredEntries(storedEntries - 1);
                delete(successor.key, node.rightChild, offset, "R");
            }
        }
    }

    private long updatePointer(long offset, String leftOrRight) throws IOException {
        long pointer = findEmptySlot();
        updatePointer(offset, pointer, leftOrRight);
        return pointer;
    }
    private void updatePointer(long offset, long nodePointer, String leftOrRight) throws IOException {
        if (leftOrRight.equalsIgnoreCase("L")){
            accessFile.seek(offset + FLAG_SIZE + keyCodec.maxByteSize());
            accessFile.writeLong(nodePointer);
        }
        else if (leftOrRight.equalsIgnoreCase("R")){
            accessFile.seek(offset + FLAG_SIZE + keyCodec.maxByteSize() + CHILD_REFERENCE_SIZE);
            accessFile.writeLong(nodePointer);
        }
    }

    private long findEmptySlot() throws IOException {
        long offset = STORED_ENTRIES_SIZE;
        while (accessFile.read() == EXIST_FLAG){
            offset += maxSizeOfEntry;
        }
        return offset;
    }

    private TreeNode_DualValue_Pointers<K,V> leftMost(long offset) throws IOException {
        accessFile.seek(offset + FLAG_SIZE);
        TreeNode_Pointers<K> node = treeCodec.read(accessFile);
        if (node.leftChild == 0){
            V value = valueCodec.read(accessFile);
            return new TreeNode_DualValue_Pointers<>(node, value);
        }
        return leftMost(node.leftChild);
    }

    private void updateStoredEntries(int storedEntries) throws IOException {
        accessFile.seek(0);
        accessFile.write(storedEntries);
    }
}
