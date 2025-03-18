package gr.bookapp.storage.file;

import gr.bookapp.storage.codec.Codec;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class FileBasedNodeStorageMap<K,V> implements NodeStorageMap<K,V> {
    private final RandomAccessFile accessFile;
    private final Codec<K> keyCodec;
    private final Codec<V> valueCodec;

    public static final byte EXIST_FLAG = 1;
    public static final byte NULL_FLAG = 0;
    public static final byte FLAG_SIZE = 1;
    public static final byte NEXT_OFFSET_SIZE = 8;
    public static final byte CHILD_REFERENCE_SIZE = 8;
    public static final byte STORED_ENTRIES_SIZE = 4;

    private final int maxSizeOfEntry;
    private int maxAvailableEntries;
    private int storedEntries;

    public FileBasedNodeStorageMap(Path path, Codec<K> keyCodec, Codec<V> valueCodec) throws IOException {
        accessFile = new RandomAccessFile(path.toFile(), "rw");
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
        maxSizeOfEntry = FLAG_SIZE + NEXT_OFFSET_SIZE + keyCodec.maxByteSize() + valueCodec.maxByteSize() + CHILD_REFERENCE_SIZE * 2;
        if (accessFile.length() == 0){
            maxAvailableEntries = 16;
            accessFile.setLength((long) maxSizeOfEntry * maxAvailableEntries + STORED_ENTRIES_SIZE);
        }
        else {
            accessFile.seek(0);
            storedEntries = accessFile.readInt();
            maxAvailableEntries = (int) ((accessFile.length() - STORED_ENTRIES_SIZE) / maxSizeOfEntry);
        }
    }

    @Override
    public boolean isNull(long nodeOffset) {
        try {
            accessFile.seek(nodeOffset);
            return accessFile.read() == NULL_FLAG;
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    @Override
    public long calculateOffset(K key) {
        long hash = Math.abs(key.hashCode()) % maxAvailableEntries;
        return hash * maxSizeOfEntry + STORED_ENTRIES_SIZE;
    }

    @Override
    public boolean matchKey(long nodeOffset, K key) {
        try {
            accessFile.seek(nodeOffset + FLAG_SIZE + NEXT_OFFSET_SIZE);
            return keyCodec.read(accessFile).equals(key);
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    @Override
    public K readKey(long nodeOffset) {
        try {
            accessFile.seek(nodeOffset + FLAG_SIZE + NEXT_OFFSET_SIZE);
            return keyCodec.read(accessFile);
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    @Override
    public V readValue(long nodeOffset) {
        try {
            accessFile.seek(nodeOffset + FLAG_SIZE + NEXT_OFFSET_SIZE + keyCodec.maxByteSize());
            return valueCodec.read(accessFile);
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    @Override
    public void writeValue(long nodeOffset, V value) {
        try {
            accessFile.seek(nodeOffset + FLAG_SIZE + NEXT_OFFSET_SIZE + keyCodec.maxByteSize());
            valueCodec.write(accessFile, value);
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    @Override
    public long readNextOffset(long nodeOffset) {
        try {
            accessFile.seek(nodeOffset + FLAG_SIZE);
            return accessFile.readLong();
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    @Override
    public void writeNode(K key, V value, long offset) {
        try {
            accessFile.seek(offset);
            accessFile.write(EXIST_FLAG);
            accessFile.skipBytes(NEXT_OFFSET_SIZE);
            keyCodec.write(accessFile, key);
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
    public void updateNextOffset(long parentOffset, long childOffset) {
        try {
            accessFile.seek(parentOffset + FLAG_SIZE);
            accessFile.writeLong(childOffset);
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
    public Iterator<Map.Entry<K, V>> entriesIterator() {
        return new Iterator<Map.Entry<K, V>>() {
            long offset = STORED_ENTRIES_SIZE;
            int remainingSlots = maxAvailableEntries;
            Map.Entry<K,V> next = findNext();

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Map.Entry<K, V> next() {
                var curr = next;
                next = findNext();
                return curr;
            }

            private Map.Entry<K, V> findNext() {
                if (remainingSlots == 0) return null;
                if (isNull(offset)){
                    offset += maxSizeOfEntry;
                    remainingSlots--;
                    return findNext();
                }
                K key = readKey(offset);
                V value = readValue(offset);
                offset += maxSizeOfEntry;
                remainingSlots--;
                return Map.entry(key,value);
            }
        };
    }

    @Override
    public long findEmptySlot(long offset) {
        for (int i = 0; i < maxAvailableEntries; i++) {
            if (offset == 0) offset = STORED_ENTRIES_SIZE;
            if (pointerExceedRange(offset)) offset = STORED_ENTRIES_SIZE;
            if (isNull(offset)) return offset;
            offset += maxSizeOfEntry;
        }
        throw new IllegalStateException("Couldn't find empty slot in the access file");
    }

    @Override
    public boolean isFull(){return storedEntries == maxAvailableEntries;}

    @Override
    public HashMap<K,V> resize(){
        try {
            long prevLength = accessFile.length();
            HashMap<K,V> map = loadPrevEntries(prevLength);
            maxAvailableEntries *= 2;
            long newLength = (long) maxAvailableEntries * maxSizeOfEntry + STORED_ENTRIES_SIZE;
            accessFile.setLength(newLength);
            storedEntries = 0;
            return map;
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    private HashMap<K, V> loadPrevEntries(long prevLength) {
        HashMap<K,V> map = new HashMap<>(storedEntries);
        for (long offset = STORED_ENTRIES_SIZE; offset < prevLength; offset += maxSizeOfEntry) {
            try {
                accessFile.seek(offset + FLAG_SIZE + NEXT_OFFSET_SIZE);
                K key = keyCodec.read(accessFile);
                map.put(key, readValue(offset));
                deleteNode(offset);
            } catch (IOException e) {throw new RuntimeException(e);}
        }
        return map;
    }


    private boolean pointerExceedRange(long pointer) {
        try {
            return pointer >= accessFile.length() - STORED_ENTRIES_SIZE;
        } catch (IOException e) {throw new RuntimeException(e);}
    }


    private int getMaxAvailableEntries() { return maxAvailableEntries; }
}
