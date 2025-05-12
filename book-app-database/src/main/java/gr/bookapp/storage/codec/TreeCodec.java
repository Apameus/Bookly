package gr.bookapp.storage.codec;

import gr.bookapp.protocol.codec.StreamCodec;
import java.io.IOException;
import java.io.RandomAccessFile;

public record TreeCodec<K,V>(StreamCodec<K> keyCodec, StreamCodec<V> valueCodec) implements FileCodec<TreeNodeDual<K,V>> {

    @Override
    public int maxByteSize() {
        return keyCodec.maxByteSize() + Long.BYTES + Long.BYTES + valueCodec.maxByteSize();
    }
    public int keyByteSize() {
        return keyCodec.maxByteSize() + Long.BYTES + Long.BYTES;
    }

    @Override
    public TreeNodeDual<K, V> read(RandomAccessFile accessFile) throws IOException {
        long filePointer = accessFile.getFilePointer();
        K key = keyCodec.read(accessFile);
        accessFile.seek(filePointer + keyCodec.maxByteSize());
        V value = valueCodec.read(accessFile);
        accessFile.seek(filePointer + keyCodec.maxByteSize() + Long.BYTES * 2 + valueCodec.maxByteSize());
        return new TreeNodeDual<>(key, accessFile.readLong(), accessFile.readLong(), value);
    }

    public TreeNode<K> readKeyEntry(RandomAccessFile accessFile) throws IOException {
        long filePointer = accessFile.getFilePointer();
        K key = keyCodec.read(accessFile);
        accessFile.seek(filePointer + keyCodec.maxByteSize());
        return new TreeNode<>(key, accessFile.readLong(), accessFile.readLong());
    }

    @Override
    public void write(RandomAccessFile accessFile, TreeNodeDual<K, V> node) throws IOException {
        long filePointer = accessFile.getFilePointer();

        keyCodec.write(accessFile, node.key());
        accessFile.seek(filePointer + keyCodec.maxByteSize());

        accessFile.writeLong(node.leftPointer());
        accessFile.writeLong(node.rightPointer());

        valueCodec.write(accessFile, node.value());
        accessFile.seek(filePointer + keyCodec.maxByteSize() + Long.BYTES * 2 + valueCodec.maxByteSize());
    }

    public void write(RandomAccessFile accessFile, K key, V value) throws IOException {
        write(accessFile, new TreeNodeDual<>(key, value));
    }
}
