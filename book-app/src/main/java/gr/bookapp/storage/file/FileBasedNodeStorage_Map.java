package gr.bookapp.storage.file;

import gr.bookapp.storage.codec.Codec;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

public final class FileBasedNodeStorage_Map<K,V> implements NodeStorage_Map<K,V>{
    private final RandomAccessFile accessFile;
    private final Codec<K> keyCodec;
    private final Codec<V> valueCodec;

    public static final byte EXIST_FLAG = 1;
    public static final byte NULL_FLAG = 0;
    public static final byte FLAG_SIZE = 1;
    public static final byte NEXT_OFFSET_SIZE = 8;
    public static final byte STORED_ENTRIES_SIZE = 4;

    private final int maxSizeOfEntry;
    private int availableEntries;
    private int storedEntries;

    public FileBasedNodeStorage_Map(Path path, Codec<K> keyCodec, Codec<V> valueCodec) throws IOException {
        accessFile = new RandomAccessFile(path.toFile(), "rw");
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
        maxSizeOfEntry = FLAG_SIZE + NEXT_OFFSET_SIZE + keyCodec.maxByteSize() + valueCodec.maxByteSize();
        if (accessFile.length() == 0){
            availableEntries = 16;
            accessFile.setLength((long) maxSizeOfEntry * availableEntries + STORED_ENTRIES_SIZE);
        }
        else {
            storedEntries = accessFile.readInt();
            availableEntries = (int) ((accessFile.length() - STORED_ENTRIES_SIZE) / maxSizeOfEntry);
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
    public long calculateOffset(K key) {
        long hash = Math.abs(key.hashCode()) % availableEntries;
        return hash * maxSizeOfEntry + STORED_ENTRIES_SIZE;
    }


    // START FROM START
    @Override
    public boolean pointerExceedRange(long pointer) { //todo
        try {
            return pointer >= accessFile.length() - STORED_ENTRIES_SIZE;
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    @Override
    public boolean matchKey(long nodeOffset, K key) {
        try {
            accessFile.seek(nodeOffset + FLAG_SIZE + NEXT_OFFSET_SIZE);
            return keyCodec.read(accessFile).equals(key);
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
            if (isFull()) resize();
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
            accessFile.writeInt(storedEntries + by);
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    @Override
    public long findEmptySlot(long offset) {
        for (int i = 0; i < availableEntries; i++) {
            if (pointerExceedRange(offset)) offset = rootOffset();
            if (isNull(offset)) return offset;
            offset += maxSizeOfEntry;
        }
        throw new IllegalStateException("Couldn't find empty slot in the access file");
    }


    public boolean isFull(){return storedEntries == availableEntries;}
    public void resize(){

    }
}
