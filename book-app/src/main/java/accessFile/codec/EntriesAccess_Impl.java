package accessFile.codec;

import accessFile.EntriesAccess;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.Comparator;

public final class EntriesAccess_Impl<K,V> implements EntriesAccess<K,V> {

    private final RandomAccessFile accessFile;
    private final TreeCodecDual<K,V> treeCodec;
    private final Codec<K> keyCodec;
    private final Codec<V> valueCodec;
    private final Comparator<K> comparator;

    private final byte EXIST_FLAG = 1;
    private final byte NULL_FLAG = 0;
    private final byte FLAG_SIZE = 1;
    private final byte CHILD_REFERENCE_SIZE = 8;
    private final byte STORED_ENTRIES_SIZE = 4;
    private final int maxSizeOfEntry;

    private int storedEntries;
    private int availableEntries;

    public EntriesAccess_Impl(Path path, Codec<K> keyCodec, Codec<V> valueCodec, Comparator<K> comparator) throws IOException {
        accessFile = new RandomAccessFile(path.toFile(), "rw");
        treeCodec = new TreeCodecDual<>(keyCodec, valueCodec);
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
        this.comparator = comparator;

        availableEntries = 16;
        maxSizeOfEntry = FLAG_SIZE + treeCodec.maxByteSize();
        if (accessFile.length() == 0) accessFile.setLength((long) maxSizeOfEntry * availableEntries + STORED_ENTRIES_SIZE);
    }

    @Override
    public boolean hasExistFlag(long offset) throws IOException {
        accessFile.seek(offset);
        return accessFile.read() == EXIST_FLAG;
    }

    @Override
    public void updateFlag(long offset, byte flag) throws IOException {
        accessFile.seek(offset);
        accessFile.write(flag);
    }

    @Override
    public int maxEntrySize() {
        return maxSizeOfEntry;
    }

    @Override
    public int storedEntries() {
        return storedEntries;
    }

    @Override
    public void updateStoredEntries(int storedEntries) throws IOException {
        accessFile.seek(0);
        accessFile.writeInt(storedEntries);
    }

    @Override
    public void resize() throws IOException {
        availableEntries *= 2;
        accessFile.setLength((long) availableEntries * maxSizeOfEntry + STORED_ENTRIES_SIZE);
    }

    @Override
    public boolean isFull() {
        return availableEntries == storedEntries;
    }

    @Override
    public K readKey(long offset) throws IOException {
        return keyCodec.read(accessFile);
    }

    @Override
    public V readValue(long offset) throws IOException {
        accessFile.seek(offset + FLAG_SIZE + treeCodec.singleNodeByteSize());
        return valueCodec.read(accessFile);
    }
    @Override
    public V readValue() throws IOException {
        return valueCodec.read(accessFile);
    }

    @Override
    public TreeNode_DualValue_Pointers<K, V> readFullEntry(long offset) throws IOException {
        if (!hasExistFlag(offset)) return null;
        return treeCodec.read(accessFile);
    }

    @Override
    public TreeNode_Pointers<K> readKeyEntry(long offset) throws IOException {
        if (!hasExistFlag(offset)) return null;
        return treeCodec.readKeyNode(accessFile);
    }
    @Override
    public TreeNode_Pointers<K> readKeyEntry() throws IOException { //TODO: Should this be to the EntriesAccess Interface ???
        return treeCodec.readKeyNode(accessFile);
    }

    @Override
    public void writeNewEntry(K key, V value, long offset) throws IOException {
        updateFlag(offset, EXIST_FLAG);
        treeCodec.write(accessFile, key, value);
    }

    @Override
    public void updateKeyValue(long nodePointer, K newKey, V newValue) throws IOException {
        accessFile.seek(nodePointer + FLAG_SIZE);
        keyCodec.write(accessFile, newKey);
        accessFile.skipBytes(CHILD_REFERENCE_SIZE * 2);
        valueCodec.write(accessFile, newValue);
    }

    @Override
    public long updatePointer(long offset, String leftOrRight) throws IOException {
        long pointer = findEmptySlot();
        updatePointer(offset, pointer, leftOrRight);
        return pointer;
    }

    @Override
    public void updatePointer(long offset, long nodePointer, String leftOrRight) throws IOException {
        if (leftOrRight.equalsIgnoreCase("L")){
            accessFile.seek(offset + FLAG_SIZE + keyCodec.maxByteSize());
            accessFile.writeLong(nodePointer);
        }
        else if (leftOrRight.equalsIgnoreCase("R")){
            accessFile.seek(offset + FLAG_SIZE + keyCodec.maxByteSize() + CHILD_REFERENCE_SIZE);
            accessFile.writeLong(nodePointer);
        }
    }

    @Override
    public int compare(K a, K b){
        return comparator.compare(a, b);
    }

    @Override
    public long findEmptySlot() throws IOException {
        long offset = STORED_ENTRIES_SIZE;
        while (hasExistFlag(offset)){
            offset += maxSizeOfEntry;
            accessFile.seek(offset);
        }
        return offset;
    }

    @Override
    public TreeNode_DualValue_Pointers<K, V> leftMost(long nodePointer) throws IOException {
        var node = readKeyEntry(nodePointer);
        if (node.leftPointer == 0) return new TreeNode_DualValue_Pointers<>(node, readValue(nodePointer));
        return leftMost(node.leftPointer);
    }




}
